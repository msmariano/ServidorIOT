package br.com.neuverse.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import br.com.neuverse.enumerador.Status;
import br.com.neuverse.enumerador.TipoIOT;
import br.com.neuverse.principal.Cliente;

public class Conector {
	
	@Expose(serialize = true)
	private String idConector;
	@Expose(serialize = true)
	private String usuario;
	@Expose(serialize = true)
	private String senha;
	@Expose(serialize = true)
	private String ip;
	@Expose(serialize = true)
	private String nome;
	@Expose(serialize = true)
	private Iot iot;
	@Expose(serialize = true)
	private ControllerIot controlerIot;
	@Expose(serialize = true)
	private TipoIOT tipo;
	@Expose(serialize = true)
	private Status status;
	@Expose(serialize = true)
	private Mensagem mens;
	@Expose(serialize = true)
	private String erro;
	@Expose(serialize = true)
	private List<String> iots;
	@Expose(serialize = true)
	private String acao;
	@Expose(serialize = true)
	private String mac;
	@Expose(serialize = true)
	private String reqRet;
	@Expose(serialize = true)
	private String timeStampAlive;
	@Expose(serialize = true)
	private List<Conector> conectores;
	@Expose(serialize = false)
	private List<Device> devices;
	@Expose(serialize = true)
	private List<ButtonIot> buttons = new ArrayList<>();
	@Expose(serialize = false)
	private Cliente cliente;
		

	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public List<ButtonIot> getButtons() {
		return buttons;
	}
	public void setButtons(List<ButtonIot> buttons) {
		this.buttons = buttons;
	}
	public List<Device> getDevices() {
		return devices;
	}
	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
	public String getIdConector() {
		return idConector;
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
	public void setIdConector(String idConector) {
		this.idConector = idConector;
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
	public class ConectorSessaoServidor{
		private String endServidor;
		private Integer porta;
	}
	
		
}
