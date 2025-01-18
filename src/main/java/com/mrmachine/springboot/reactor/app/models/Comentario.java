package com.mrmachine.springboot.reactor.app.models;

import java.util.ArrayList;
import java.util.List;

public class Comentario {
	private List<String> comentarios;
	
	public Comentario() {
		this.comentarios = new ArrayList<String>();
	}

	public List<String> getComentarios() {
		return comentarios;
	}

	public void addComentarios(String comentario) {
		this.comentarios.add(comentario);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Comentario [comentarios=");
		builder.append(comentarios);
		builder.append("]");
		return builder.toString();
	}
	
}
