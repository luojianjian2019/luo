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
	
	//�����˵�ַ
    public static String senderAddress = "13852242374@163.com";
    //�ռ��˵�ַ
   // public static String recipientAddress = "1475859572@qq.com";
    //�������˻���
    public static String senderAccount = "13852242374@163.com";
    //�������˻�����
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
        		if(today.get(Calendar.MONTH)==birthday.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH)==birthday.get(Calendar.DAY_OF_MONTH)){ //�ж� birthday is today
        			
        			new Thread(){
        				public void run(){
        					//1�������ʼ��������Ĳ�������
        			        Properties props = new Properties();
        			        //�����û�����֤��ʽ
        			        props.setProperty("mail.smtp.auth", "true");
        			        //���ô���Э��
        			        props.setProperty("mail.transport.protocol", "smtp");
        			        //���÷����˵�SMTP��������ַ
        			        props.setProperty("mail.smtp.host", "smtp.163.com");
        			        //2��������������Ӧ�ó�������Ļ�����Ϣ�� Session ����
        			        Session session = Session.getInstance(props);
        			        //���õ�����Ϣ�ڿ���̨��ӡ����
        			        session.setDebug(true);
        			        //3�������ʼ���ʵ������
        			        Message msg = null;
							try {
								msg = getMimeMessage(session,e.getLastName(),e.getEamil());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        			        //4������session�����ȡ�ʼ��������Transport
        			        Transport transport=null;
							try {
								transport = session.getTransport();
							} catch (NoSuchProviderException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        			        //���÷����˵��˻���������
        			        try {
								transport.connect(senderAccount, senderPassword);
							} catch (MessagingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        			        //�����ʼ��������͵������ռ��˵�ַ
        			        try {
								transport.sendMessage(msg,msg.getAllRecipients());
							} catch (MessagingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
        			         
        			        //���ֻ�뷢�͸�ָ�����ˣ���������д��
        			        //transport.sendMessage(msg, new Address[]{new InternetAddress("xxx@qq.com")});
        			         
        			        //5���ر��ʼ�����
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
	
	//���ļ�
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
     * ��ô���һ���ʼ���ʵ������
     * @param session
     * @return
     * @throws MessagingException
     * @throws AddressException
     */
    public static MimeMessage getMimeMessage(Session session,String name,String address) throws Exception{
        //����һ���ʼ���ʵ������
        MimeMessage msg = new MimeMessage(session);
        //���÷����˵�ַ
        msg.setFrom(new InternetAddress(senderAddress));
        /**
         * �����ռ��˵�ַ���������Ӷ���ռ��ˡ����͡����ͣ�����������һ�д�����д����
         * MimeMessage.RecipientType.TO:����
         * MimeMessage.RecipientType.CC������
         * MimeMessage.RecipientType.BCC������
         */
        msg.setRecipient(MimeMessage.RecipientType.TO,new InternetAddress(address));
        msg.setRecipient(MimeMessage.RecipientType.CC,new InternetAddress(senderAddress));
        //�����ʼ�����
        msg.setSubject("���տ���!","UTF-8"); //��Ӣ�� Happy birthday���ղ����ʼ���
        //�����ʼ�����
        msg.setContent("Happy birthday,dear "+name, "text/html;charset=UTF-8");
        //�����ʼ��ķ���ʱ��,Ĭ����������
        msg.setSentDate(new Date());
         
        return msg;
    }

}
