package com.oracle.jdbc.util;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


/**
 * ��ô�������ڲ�ʹ��cglib��ʵ�ֵģ�֧�������Զ��򿪹ر�����
 * @author Administrator
 *
 */
public class ServiceFactory implements MethodInterceptor{
	Enhancer e=new Enhancer();  //���������������

	@Override
	public Object intercept(Object proxy, Method method, Object[] args, MethodProxy arg3) throws Throwable {
		Object obj=null;
		
		System.out.println(method.getName());
		if(method.isAnnotationPresent(Transactional.class)){
			System.out.println("transactional");
			try{
				Dao.begin();
				obj=arg3.invokeSuper(proxy, args);				
				Dao.commit();		
			}catch(Exception e){
				e.printStackTrace();
				Dao.rollback();				
			}finally{				
				Dao.close();
			}	
		}else{
			try{
				obj=arg3.invokeSuper(proxy, args);
			}finally{
				Dao.close();
			}	
		}
		return obj;
	}
	
	//���ɴ������
	@SuppressWarnings("unchecked")
	public <T> T getProxy(Class<T> clazz){
		T obj=null;
		try {
			obj = clazz.newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		e.setSuperclass(obj.getClass()); //������˭
		e.setCallback(this); //���ûص��������ô������ķ���ʱ��������this��intercepter����
		return (T) e.create();  //���ش������
	}
	
	
	public static <T> T getObject(Class<T> clazz){
		return new ServiceFactory().getProxy(clazz);
	}
	

}
