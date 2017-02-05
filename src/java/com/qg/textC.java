package com.qg;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zggdczfr on 2017/1/9.
 */
public class textC {
    //运行进程
    static Process p;
    /**
     * 过 Runtime 调用运行 exe 文件
     * @param filePath exe 文件绝对路径
     * @param inputString 程序读取数据
     * @throws InterruptedException
     * @throws FileNotFoundException
     */
    static public void openApplication(String filePath, final String inputString) throws InterruptedException, FileNotFoundException {
        File file = new File(filePath);
        if(!file.exists()){
            throw new FileNotFoundException("找不到exe文件！");
        }

        try {
            p = Runtime.getRuntime().exec(filePath);
            //exe程序数据输出流
            //相当于进程标准输入流
            final BufferedInputStream output = new BufferedInputStream(p.getInputStream());
            //exe程序数据输入流
            final BufferedOutputStream input = new BufferedOutputStream(p.getOutputStream());
            //exe程序错误输出流
            final BufferedInputStream errorOutput = new BufferedInputStream(p.getErrorStream());

            final StringBuffer outputText = new StringBuffer("获得信息是: \n");
            //final StringBuffer inputText = new StringBuffer(inputString);
            final StringBuffer errorText = new StringBuffer("错误信息是：\n");

            /**
             * 向线程进行输入
             */
            new Thread(){
                public void run(){
                    try {
                        System.out.println("执行输入！\n");
                        //将用户输入数据写入
                        input.write(inputString.getBytes());
                        input.flush();//清空存缓
                        System.out.println("----\n读入完毕\n---\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(input != null) {
                            try {
                                input.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();

            /**
             * 获得输出的线程
             */
            new Thread(){
                public void run(){
                    int ch;
                    try {
                        System.out.println("执行输出！\n");
                        //不断获取用户输出
                        while ((ch = output.read()) != -1) {
                            outputText.append((char) ch);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (output != null){
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();

            /**
             * 获得进程的错误提示
             */
            new Thread(){
                public void run(){
                    int ch;
                    try {
                        System.out.println("执行错误输出！\n");
                        //不断获取错误输出
                        while ((ch = errorOutput.read()) != -1) {
                            System.out.println((char) ch);
                            errorText.append((char) ch);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(errorOutput != null){
                            try {
                                errorOutput.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();


            /**
             * 控制时间的进程
             */
            Thread timeController = new Thread(){
                public void run(){
                    try {
                        System.out.println("执行时间控制！\n");
                        Thread.sleep(5000); //限制运行时间
                        //加入错误提示信息
                        errorText.append("\n运行时间过长！\n");
                        p.destroy();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if(p != null) {
                            p.destroy();
                        }
                    }
                }
            };
            timeController.start();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            //记录执行时间
            System.out.println("\n开始执行时间："+df.format(new Date()));// new Date()为获取当前系统时间
            //一直等待直到“启动成功”
            int   retval   =   p.waitFor();
            //waitfor()结束后，关闭时间控制进程
            timeController.stop();
            //记录结束时间
            System.out.println("\n结束执行时间："+df.format(new Date()));// new Date()为获取当前系统时间

            System.out.println(outputText);
            System.out.println(errorText);
            System.out.println(retval);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(p!=null) {
                p.destroy();
            }
        }
    }

    //编译进程
    static Process c;
    /**
     * 通过 Runtime 调用批处理文件来编译编源文件
     * 注意：批处理文件需要与编译源文件位于同一目录下
     * @param filePath 批处理文件的绝对路径
     * @throws FileNotFoundException 找不到
     */
    static public void compileApplication(String filePath) throws FileNotFoundException {
        Runtime run = Runtime.getRuntime();
        String cPath = filePath.replace("bat", "c");

        File batFile = new File(filePath);
        File cFile = new File(cPath);

        if(!cFile.exists()){
            throw new FileNotFoundException("找不到c编译源文件！");
        }

        if(batFile.exists()) {
            try {
                c = run.exec("cmd.exe /c " + filePath);
                InputStream in = c.getInputStream();
                BufferedInputStream errorIn = new BufferedInputStream(c.getErrorStream());
                int ch;
                StringBuffer errortext = new StringBuffer("");
                //如果有编译错误，读取错误提示
                while ((ch = errorIn.read()) != -1) {
                    errortext.append((char) ch);
                }
                //将编译错误打印出来,并抛出错误异常
                if (!errortext.equals("")) {
                    System.out.println(errortext);
                    //自定义错误异常

                }

                errorIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.destroy();
                }
            }
        } else {
            throw new FileNotFoundException("找不到cmd批处理文件！");
        }
    }


    public static void main(String[] args) throws InterruptedException {
        try {
            //编译c程序
            compileApplication("E:/test.bat");
            //运行exe文件
            openApplication("E:/test.exe", "99\n123456\n123");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
