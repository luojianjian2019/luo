package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import domain.Employee;

public class EmailTest {
	
	//发件人地址
    public static String senderAddress = "13852242374@163.com";
    //收件人地址
   // public static String recipientAddress = "1475859572@qq.com";
    //发件人账户名
    public static String senderAccount = "13852242374@163.com";
    //发件人账户密码
    public static String senderPassword = "bao123456";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
        List<Employee> empList=readFile();
        if(empList!=null && !empList.isEmpty()){
        	Calendar today=Calendar.getInstance();
        	for(final Employee e:empList){
        		Calendar birthday = Calendar.getInstance();
        		birthday.setTime(e.getBirthday());
        		if(today.get(Calendar.MONTH)==birthday.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH)==birthday.get(Calendar.DAY_OF_MONTH)){ //判断 birthday is today
        			
        			new Thread(){
        				public void run(){
        					//1、连接邮件服务器的参数配置
        			        Properties props = new Properties();
        			        //设置用户的认证方式
        			        props.setProperty("mail.smtp.auth", "true");
        			        //设置传输协议
        			        props.setProperty("mail.transport.protocol", "smtp");
        			        //设置发件人的SMTP服务器地址
        			        props.setProperty("mail.smtp.host", "smtp.163.com");
        			        //2、创建定义整个应用程序所需的环境信息的 Session 对象
        			        Session session = Session.getInstance(props);
        			        //设置调试信息在控制台打印出来
        			        session.setDebug(true);
        			        //3、创建邮件的实例对象
        			        Message msg = null;
							try {
								msg = getMimeMessage(session,e.getLastName(),e.getEamil());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        			        //4、根据session对象获取邮件传输对象Transport
        			        Transport transport=null;
							try {
								transport = session.getTransport();
							} catch (NoSuchProviderException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        			        //设置发件人的账户名和密码
        			        try {
								transport.connect(senderAccount, senderPassword);
							} catch (MessagingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        			        //发送邮件，并发送到所有收件人地址
        			        try {
								transport.sendMessage(msg,msg.getAllRecipients());
							} catch (MessagingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        			         
        			        //如果只想发送给指定的人，可以如下写法
        			        //transport.sendMessage(msg, new Address[]{new InternetAddress("xxx@qq.com")});
        			         
        			        //5、关闭邮件连接
        			        try {
								transport.close();
							} catch (MessagingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        				}
        			}.start();

					
					
        		}
        	}
        }
   
	}
	
	//读文件
	public static List<Employee> readFile() {
        File file=new File("d:/employee_records.txt");
        BufferedReader reader=null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        List<Employee> empList=new ArrayList<Employee>();
        try{
        	reader = new BufferedReader(new FileReader(file)); 
        	String tempString = null; 
        	//int i=1;
        	while ((tempString = reader.readLine()) != null) {
        		//System.out.println("line " + i + ": " + tempString);
        		//i++;
        		String[] tempArr=tempString.split(",");
        		if(!tempArr[0].equals("last_name")){
        			Employee e=new Employee();
            		e.setFirstName(tempArr[1]);
            		e.setLastName(tempArr[0]);
            		e.setBirthday(sdf.parse(tempArr[2]));
            		e.setEamil(tempArr[3]);
            		empList.add(e);
        		}
        	}
        	return empList;
        }catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
        	return null;
		}finally{
			if(reader!=null){
				try{
					reader.close();
				}catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
    }
	
	/**
     * 获得创建一封邮件的实例对象
     * @param session
     * @return
     * @throws MessagingException
     * @throws AddressException
     */
    public static MimeMessage getMimeMessage(Session session,String name,String address) throws Exception{
        //创建一封邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //设置发件人地址
        msg.setFrom(new InternetAddress(senderAddress));
        /**
         * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */
        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(address));
        msg.setRecipient(MimeMessage.RecipientType.CC,new InternetAddress(senderAddress));
        //设置邮件主题
        msg.setSubject("Happy birthday!","UTF-8");
        //设置邮件正文
        msg.setContent("Happy birthday,dear "+name, "text/html;charset=UTF-8");
        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());
         
        return msg;
    }

}
