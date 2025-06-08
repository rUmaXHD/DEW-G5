package dew.main.structures;

public class Asignatura {
	String acronimo;
	String nombre;
	int curso;
	String cuatrimestre;
	float creditos;
	
	public Asignatura() {}
	
	public Asignatura(String acronimo, String nombre, int curso, String cuatrimestre, float creditos) {
		this.acronimo = acronimo;
		this.nombre = nombre;
		this.curso = curso;
		this.cuatrimestre = cuatrimestre;
		this.creditos = creditos;
	}
	public String getAcronimo() {
		return acronimo;
	}
	public void setAcronimo(String acronimo) {
		this.acronimo = acronimo;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getCurso() {
		return curso;
	}
	public void setCurso(int curso) {
		this.curso = curso;
	}
	public String getCuatrimestre() {
		return cuatrimestre;
	}
	public void setCuatrimestre(String cuatrimestre) {
		this.cuatrimestre = cuatrimestre;
	}
	public float getCreditos() {
		return creditos;
	}
	public void setCreditos(float creditos) {
		this.creditos = creditos;
	}
}