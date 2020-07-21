package com.orchidblessing.library.test;
import java.util.Scanner;
import java.util.Random;
import java.util.Arrays;

/**
 双色球模拟：
 红球01~33 选6
 蓝球01~16 选1
 0.准备相关变量
 1.用户选择机选或手选
 2.接收用户号码
 3.生成系统号码
 4.比较系统号码和用户号码，记录个数
 5.验证是否中奖
 6.系统号码排序
 7.公布结果

 一等奖 6+1
 二等奖 6+0
 三等奖 5+1
 四等奖 5+0||4+1
 五等奖 4+0||3+1
 六等奖 2+1||1+1||0+1
 无 3+0||2+0||1+0||0+0
 */
public class TestLesson {
    public static void main(String[] args) {
        //定义相关变量
        int[] userRedBall=new int[6];//用户选择的红球号码
        int[] sysRedBall=new int[6];//系统选择的红球
        int userBlueBall=0;//用户选择的蓝球
        int sysBlueBall=0;//系统选择的蓝球
        int redCount=0;//用户选择正确的红球数
        int blueCount=0;//用户选择正确的蓝球数

        //游戏开始,系统提示
        System.out.println("双色球游戏开始，Good luck！");
        System.out.println("请问是要机选还是手选（1.机选 2.手选）");
        Scanner input=new Scanner(System.in);
        boolean flag=true;//用于判断是否重新输入机选或手选
        while(flag){
            int isAuto=input.nextInt();
            switch(isAuto){
                case 1:
                    //机选
                    userRedBall=getRedBall();//机选红球
                    userBlueBall=getBlueBall();//机选蓝球
                    flag=false;
                    break;
                case 2:
                    //手选
                    System.out.println("请选择6个红球号码（1~33）");
                    for(int i=0;i<userRedBall.length;i++){
                        userRedBall[i]=input.nextInt();
                    }
                    System.out.println("请选择1个蓝球号码（1~16）");
                    userBlueBall=input.nextInt();
                    flag=false;
                    break;
                default:
                    System.out.println("请问是要机选还是手选（1.机选 2.手选）");
                    break;
            }
        }

        //系统随机生成奖号码
        sysRedBall=getRedBall();//机选中奖红球
        sysBlueBall=getBlueBall();//机选中奖蓝球

        //统计结果
        //统计红球
        for(int i=0;i<userRedBall.length;i++){//依次拿用户红球
            for(int j=0;j<sysRedBall.length-redCount;j++){//与每个系统红球比较
                if(userRedBall[i]==sysRedBall[j]){
                    int tem=sysRedBall[j];
                    sysRedBall[j]=sysRedBall[sysRedBall.length-1-redCount];
                    sysRedBall[sysRedBall.length-1-redCount]=tem;
                    redCount++;
                }
            }
        }
        //统计蓝球
        if(userBlueBall==sysBlueBall){
            blueCount++;
        }

        //验证是否中奖，成功率高的排前面
        if(blueCount==0&&redCount<=3){
            System.out.println("没中奖");
        }else if(blueCount==1&&redCount<3){
            System.out.println("中了六等奖，5元");
        }else if((blueCount==1&&redCount==3)||(blueCount==0&&redCount==4)){
            System.out.println("中了五等奖，10元");
        }else if((blueCount==1&&redCount==4)||(blueCount==0&&redCount==5)){
            System.out.println("中了四等奖，200元");
        }else if(blueCount==1&&redCount==5){
            System.out.println("中了三等奖，3000元");
        }else if(blueCount==0&&redCount==6){
            System.out.println("中了二等奖，150w元");
        }else if(blueCount==1&&redCount==6){
            System.out.println("中了一等奖，500w元");
        }else{
            System.out.println("系统有误，中奖无效！");
        }

        //输出系统号码
        System.out.println("本期中奖红球号码为：");
        sysRedBall=sort(sysRedBall);
        System.out.println(Arrays.toString(sysRedBall));
        System.out.println("本期中奖蓝球号码为：");
        System.out.println(sysBlueBall);
        //输出用户号码
        System.out.println("本期用户红球号码为：");
        userRedBall=sort(userRedBall);
        System.out.println(Arrays.toString(userRedBall));
        System.out.println("本期用户蓝球号码为：");
        System.out.println(userBlueBall);

        System.out.println("买双色球，为福利事业做贡献！谢谢！");
    }


    //getRedBall方法：随机得到6个红球,以数组返回。（需要随机生成6个在1~33之间不重复的数（算法））
    public static int[] getRedBall(){
        //int[] redBall={1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33};
        int[] redBall=new int[33];//获得储存红球号码的数组
        for(int i=0;i<redBall.length;i++){
            redBall[i]=i+1;
        }

        //从1-33里随机生成多个不重复的数
        int index=0;//储存随机到的下标
        Random r=new Random();
        int indexMax=redBall.length;//随机范围

        for(int i=0;i<6;i++){
            index=r.nextInt(indexMax);//生成0~32的随机数作为选中的下标，其中indexMax每轮自减1

            if(index!=indexMax-1){//如果随机到的下标不是最后一个，就和最后一个交换
                int tem=0;
                tem=redBall[index];//把随机到的数和最后一个数换位置，然后随机范围-1
                redBall[index]=redBall[indexMax-1];//最后一个下标是inedxMax-1
                redBall[indexMax-1]=tem;
                indexMax--;
            }else{
                indexMax--;//如果随机到最后一个数，直接-1
            }

        }

        //取出数组的第27~32下标的数并从小到大排序
        int[] reRedBall=new int[6];
        for(int i=0;i<6;i++){
            reRedBall[i]=redBall[i+27];
        }
        Arrays.sort(reRedBall);
        return reRedBall;
    }

    //getBlueBall方法：随机得到1个蓝球，以int返回。
    public static int getBlueBall(){
        //生成蓝球库，虽然直接随机一个数就好了
        int[] blueBall=new int[16];
        for(int i=0;i<16;i++){
            blueBall[i]=i+1;
        }

        int reBlueBall;//随机一个蓝球并返回
        Random r=new Random();
        int index;//储存下标
        index=r.nextInt(16);
        reBlueBall=blueBall[index];
        return reBlueBall;
    }

    //冒泡排序
    public static int[] sort(int[] ball){
        //循环次数为球数-1
        for(int i=0;i<ball.length-1;i++){
            for(int j=0;j<ball.length-1-i;j++){
                if(ball[j]>ball[j+1]){
                    ball[j]=ball[j]+ball[j+1];
                    ball[j+1]=ball[j]-ball[j+1];
                    ball[j]=ball[j]-ball[j+1];
                }
            }
        }
        return ball;
    }
}
