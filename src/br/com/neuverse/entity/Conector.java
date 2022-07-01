package br.com.neuverse.entity;

import java.util.List;

import br.com.neuverse.enumerador.Status;
import br.com.neuverse.enumerador.TipoIOT;

public class Conector {
	
	private String id;
	private String usuario;
	private String senha;
	private String ip;
	private String nome;
	private Iot iot;
	private ControllerIot controlerIot;
	private TipoIOT tipo;
	private Status status;
	private Mensagem mens;
	private String erro;
	private List<String> iots;
	private String acao;
	private String mac;
	private String reqRet;
	private String timeStampAlive;
	private List<Conector> conectores;
	private List<Device> devices;
	

	public List<Device> getDevices() {
		return devices;
	}
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	public String getId() {
		return id;
	}
	public List<Conector> getConectores() {
		return conectores;
	}
	public void setConectores(List<Conector> conectores) {
		this.conectores = conectores;
	}
	public String getTimeStampAlive() {
		return timeStampAlive;
	}
	public void setTimeStampAlive(String timeStampAlive) {
		this.timeStampAlive = timeStampAlive;
	}
	public String getReqRet() {
		return reqRet;
	}
	public void setReqRet(String reqRet) {
		this.reqRet = reqRet;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getAcao() {
		return acao;
	}
	public void setAcao(String acao) {
		this.acao = acao;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Iot getIot() {
		return iot;
	}
	public void setIot(Iot iot) {
		this.iot = iot;
	}
	public TipoIOT getTipo() {
		return tipo;
	}
	public void setTipo(TipoIOT tipo) {
		this.tipo = tipo;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getErro() {
		return erro;
	}
	public void setErro(String erro) {
		this.erro = erro;
	}
	public ControllerIot getControlerIot() {
		return controlerIot;
	}
	public void setControlerIot(ControllerIot controlerIot) {
		this.controlerIot = controlerIot;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Mensagem getMens() {
		return mens;
	}
	public void setMens(Mensagem mens) {
		this.mens = mens;
	}
	public List<String> getIots() {
		return iots;
	}
	public void setIots(List<String> iots) {
		this.iots = iots;
	}
	
		
}
