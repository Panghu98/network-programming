package com.example.demo.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

interface FileCopyRunner {

    void copyFile(File source, File target);

}

public class FileCopyDemo {
    private static final int ROUNDS = 5;

    private static void benchmark(FileCopyRunner test, File source, File target) {
        long elapsed = 0L;
        for (int i=0; i<ROUNDS; i++) {
            long startTime = System.currentTimeMillis();
            test.copyFile(source, target);
            elapsed += System.currentTimeMillis() - startTime;
            target.delete();
        }
        System.out.println(test + ": " + elapsed / ROUNDS);
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {

        FileCopyRunner noBufferStreamCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                InputStream fin = null;
                OutputStream fout = null;
                try {
                    fin = new FileInputStream(source);
                    fout = new FileOutputStream(target);

                    int result;
                    while ((result = fin.read()) != -1) {
                        fout.write(result);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }
            }

            @Override
            public String toString() {
                return "noBufferStreamCopy";
            }
        };

        FileCopyRunner bufferedStreamCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                InputStream fin = null;
                OutputStream fout = null;
                try {
                    fin = new BufferedInputStream(new FileInputStream(source));
                    fout = new BufferedOutputStream(new FileOutputStream(target));

                    byte[] buffer = new byte[1024];

                    int result;
                    while ((result = fin.read(buffer)) != -1) {
                        fout.write(buffer, 0, result);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }
            }

            @Override
            public String toString() {
                return "bufferedStreamCopy";
            }
        };

        FileCopyRunner nioBufferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileChannel fin = null;
                FileChannel fout = null;

                try {
                    fin = new FileInputStream(source).getChannel();
                    fout = new FileOutputStream(target).getChannel();

                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    // 在Buffer中的读写操作和Channel恰好是相反的
                    while (fin.read(buffer) != -1) {
                        // 写模式装换为 读模式
                        buffer.flip();
                        // 保证
                        while (buffer.hasRemaining()) {
                            fout.write(buffer);
                        }
                        // 读完之后，再转换为写模式
                        buffer.clear();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }
            }

            @Override
            public String toString() {
                return "nioBufferCopy";
            }
        };

        // 使用Channel对Channel进行数据的传输，通道和通道之间进行树的传输
        FileCopyRunner nioTransferCopy = new FileCopyRunner() {
            @Override
            public void copyFile(File source, File target) {
                FileChannel fin = null;
                FileChannel fout = null;
                try {

                    fin = new FileInputStream(source).getChannel();
                    fout = new FileOutputStream(target).getChannel();
                    // 用于记录拷贝了多少个字节
                    long transferred = 0L;
                    long size = fin.size();
                    while (transferred != size) {
                        // transferTo,也就是重调用通道传输的参数中指定的通道当中
                        transferred += fin.transferTo(0, size, fout);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    close(fin);
                    close(fout);
                }
            }

            @Override
            public String toString() {
                return "nioTransferCopy";
            }
        };

        File smallFile = new File("/var/tmp/smallFile");
        File smallFileCopy = new File("/var/tmp/smallFile-copy");

        System.out.println("---Copying small file---");
        benchmark(noBufferStreamCopy, smallFile, smallFileCopy);
        benchmark(bufferedStreamCopy, smallFile, smallFileCopy);
        benchmark(nioBufferCopy, smallFile, smallFileCopy);
        benchmark(nioTransferCopy, smallFile, smallFileCopy);

        File bigFile = new File("/var/tmp/bigFile");
        File bigFileCopy = new File("/var/tmp/bigFile-copy");

        System.out.println("---Copying big file---");
        //benchmark(noBufferStreamCopy, bigFile, bigFileCopy);
        benchmark(bufferedStreamCopy, bigFile, bigFileCopy);
        benchmark(nioBufferCopy, bigFile, bigFileCopy);
        benchmark(nioTransferCopy, bigFile, bigFileCopy);

        File hugeFile = new File("/var/tmp/hugeFile");
        File hugeFileCopy = new File("/var/tmp/hugeFile-copy");

        System.out.println("---Copying huge file---");
        //benchmark(noBufferStreamCopy, hugeFile, hugeFileCopy);
        benchmark(bufferedStreamCopy, hugeFile, hugeFileCopy);
        benchmark(nioBufferCopy, hugeFile, hugeFileCopy);
        benchmark(nioTransferCopy, hugeFile, hugeFileCopy);

    }

}
