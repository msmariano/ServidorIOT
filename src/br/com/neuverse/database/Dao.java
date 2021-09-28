package br.com.neuverse.database;

import java.util.List;

public interface Dao <T>{
	public List<Object> listar();
	public Object obter(Integer id);
	public void deleteAll();
	public void save(T obj);
	public void save();
	public void delete(Integer id);

}
