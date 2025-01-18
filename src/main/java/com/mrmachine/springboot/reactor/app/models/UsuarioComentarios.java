package com.mrmachine.springboot.reactor.app.models;

public class UsuarioComentarios {
	
	private Usuario usuario;
	private Comentario comentarios;
	
	public UsuarioComentarios(Usuario usuario, Comentario comentarios) {
		this.usuario = usuario;
		this.comentarios = comentarios;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UsuarioComentarios [usuario=");
		builder.append(usuario);
		builder.append(", comentarios=");
		builder.append(comentarios);
		builder.append("]");
		return builder.toString();
	}
	
}
