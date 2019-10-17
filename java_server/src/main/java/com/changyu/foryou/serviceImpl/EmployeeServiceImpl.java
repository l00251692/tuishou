package com.changyu.foryou.serviceImpl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.changyu.foryou.mapper.EmployeeMapper;
import com.changyu.foryou.model.Employee;
import com.changyu.foryou.model.Hospital;
import com.changyu.foryou.service.EmployeeService;


@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {
	private EmployeeMapper employeeMapper;         //操作用户信息

	@Autowired
	public void setEmployeeMapperr(EmployeeMapper employeeMapper) {
		this.employeeMapper = employeeMapper;
	}

	
	public Employee selectByPhone(String phone) {
		return employeeMapper.selectByPrimaryKey(phone);
	}


	public int addEmployee(Map<String, Object> paramMap) {
		return employeeMapper.insertSelective(paramMap);
	}
	
	public int updateEmployee(Map<String, Object> paramMap) {
		return employeeMapper.updateEmployee(paramMap);
	}
	
	public int delEmployee(Map<String, Object> paramMap) {
		return employeeMapper.delEmployee(paramMap);
	}
	
	public List<Employee> getAllEmployee(Map<String, Object> paramMap){
		return employeeMapper.getAllEmployee(paramMap);
	}
	
	public List<Employee> selectByPhoneAndPassword(Map<String, Object> paramMap){
		return employeeMapper.selectByPhoneAndPassword(paramMap);
	}
	
	public int updatePassword(Map<String, Object> paramMap){
		return employeeMapper.updatePassword(paramMap);
	}

	
	@Override
	public Employee checkLogin(String phone) {
		return employeeMapper.checkLogin(phone);
	}
	public int updateLastLoginTime(Map<String, Object> paramMap){
		return employeeMapper.updateLastLoginTime(paramMap);
	}
	
	public List<String> getProvinceList(){
		return employeeMapper.getProvinceList();
	}
	
	public List<Hospital> getHospitalList(Map<String, Object> paramMap){
		return employeeMapper.getHospitalList(paramMap);
	}
	
	public List<Hospital> getAllHospital(Map<String, Object> paramMap){
		return employeeMapper.getAllHospital(paramMap);
	}
	
	public int addHospital(Map<String, Object> paramMap){
		return employeeMapper.addHospital(paramMap);
	}
	
	public int delHospital(Map<String, Object> paramMap){
		return employeeMapper.delHospital(paramMap);
	}
}
