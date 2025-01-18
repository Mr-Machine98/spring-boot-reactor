package com.mrmachine.springboot.reactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mrmachine.springboot.reactor.app.models.Comentario;
import com.mrmachine.springboot.reactor.app.models.Usuario;
import com.mrmachine.springboot.reactor.app.models.UsuarioComentarios;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {
	
	private static final Logger LOG = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	// Set our app like a console project.
	@Override
	public void run(String... args) throws Exception {
//		ejemploFlatMap();
//		ejemploConvertirElementosToMono();
//		ejemploConvertirCollectList();
//		ejemploUsuarioComentariosFlatMap();
//		ejemploUsuarioComentariosZipWithForma2();
//		ejemploZipWithRangos();
//		ejemploZipWithRangosDelay();
//		ejemploDelayElements();
//		ejemploIntervalInfinite();
//		ejemploIntervalDesdeCreate();\\
//		ejemploContraPresion();
		ejemploContraPresion2();
	}
	
	public void ejemploiterable1() {
		List<String> usuariosList = new ArrayList();
		usuariosList.add("Juan Mamian");
		usuariosList.add("Julian Erazo");
		usuariosList.add("Ximena Sanchez");
		usuariosList.add("Felipe Loaiza");
		usuariosList.add("Laura Arana");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Willis");
		
		/*
		 * Flux
				.just(
					"Juan Mamian",
					"Julian Erazo",
					"Ximena Sanchez",
					"Felipe Loaiza",
					"Laura Arana",
					"Bruce Lee",
					"Bruce Willis"
				);
		 * */
		
		Flux<String> nombres = Flux.fromIterable(usuariosList);
		
		Flux<Usuario> usuarios = nombres.map( elm -> new Usuario(
					elm.split(" ")[0].toUpperCase(),
					elm.split(" ")[1].toUpperCase())
				)
				.filter(usr -> usr
					.getFirstName()
					.toLowerCase()
					.equals("bruce")
				)
				.doOnNext(elem -> {
					if (elem == null) {
						throw new RuntimeException("Element shouldn't be empty");
					}
					System.out.println(elem);
				})
				.map( usr -> {
					String name = usr.getFirstName().toLowerCase();
					usr.setFirstName(name);
					return usr;
				});
		
		usuarios.subscribe(
				e -> {
					LOG.info(e.toString());
				},
				error -> {
					LOG.error(error.getMessage());
				},
				new Runnable() {
					@Override
					public void run() {
						LOG.info("The task has been finished succesfully!");
					}
				}
		);
	}
	
	public void ejemploFlatMap() {
		List<String> usuariosList = new ArrayList<String>();
		usuariosList.add("Juan Mamian");
		usuariosList.add("Julian Erazo");
		usuariosList.add("Ximena Sanchez");
		usuariosList.add("Felipe Loaiza");
		usuariosList.add("Laura Arana");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Willis");
		
		
		Flux.fromIterable(usuariosList)
				.map( elm -> new Usuario(
					elm.split(" ")[0].toLowerCase(),
					elm.split(" ")[1].toLowerCase())
				)
				.flatMap(usr -> {
					if (usr.getFirstName().equals("bruce")) {
						return Mono.just(usr);
					} else {
						return Mono.empty();
					}
				})
				.map( usr -> {
					String name = usr.getFirstName().toLowerCase();
					usr.setFirstName(name);
					return usr;
				})
				.subscribe(e -> LOG.info(e.toString()));
	}
	
	public void ejemploConvertirElementosToMono() {
		
		List<Usuario> usuariosList = new ArrayList<Usuario>();
		usuariosList.add(new Usuario("Juan", "Mamian"));
		usuariosList.add(new Usuario("Julian", "Erazo"));
		usuariosList.add(new Usuario("Ximena", "Sanchez"));
		usuariosList.add(new Usuario("Felipe", "Loaiza"));
		usuariosList.add(new Usuario("Laura", "Arana"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Willis"));
		
		
		Flux.fromIterable(usuariosList)
				.map( usr -> usr.getFirstName().toUpperCase().concat(" ").concat(usr.getLastName().toUpperCase())
				)
				.flatMap(name -> {
					if (name.contains("BRUCE")) {
						return Mono.just(name);
					} else {
						return Mono.empty();
					}
				})
				.map( name -> {
					return name.toLowerCase();
				})
				.subscribe(e -> LOG.info(e.toString()));
	}
	
	public void ejemploConvertirCollectList() {
		
		List<Usuario> usuariosList = new ArrayList<Usuario>();
		usuariosList.add(new Usuario("Juan", "Mamian"));
		usuariosList.add(new Usuario("Julian", "Erazo"));
		usuariosList.add(new Usuario("Ximena", "Sanchez"));
		usuariosList.add(new Usuario("Felipe", "Loaiza"));
		usuariosList.add(new Usuario("Laura", "Arana"));
		usuariosList.add(new Usuario("Bruce", "Lee"));
		usuariosList.add(new Usuario("Bruce", "Willis"));
		
		Flux
			.fromIterable(usuariosList)
			.collectList() // convierte un observable Flux a Mono
			.subscribe(list -> list.forEach( e -> LOG.info(e.toString())) );
	}

	public void ejemploUsuarioComentariosFlatMap() {
		
		Mono<Usuario> usrMono = Mono.fromCallable(() -> new Usuario("Juan", "Soancil"));
		
		Mono<Comentario> comentariosUsrMono = Mono.fromCallable(() -> {
			Comentario comentarios = new Comentario();
			comentarios.addComentarios("Hola sapa");
			comentarios.addComentarios("camilo se forro");
			comentarios.addComentarios("debemos conseguir plata");
			comentarios.addComentarios("i have been studying english since janaury");
			return comentarios;
		});
		
		usrMono.flatMap(u -> {
			return comentariosUsrMono.map(c -> new UsuarioComentarios(u, c));
		})
		.subscribe(uc -> LOG.info(uc.toString()));
	}
	
	public void ejemploUsuarioComentariosZipWith() {
		
		Mono<Usuario> usrMono = Mono.fromCallable(() -> new Usuario("Juan", "Soancil"));
		
		Mono<Comentario> comentariosUsrMono = Mono.fromCallable(() -> {
			Comentario comentarios = new Comentario();
			comentarios.addComentarios("Hola sapa");
			comentarios.addComentarios("camilo se forro");
			comentarios.addComentarios("debemos conseguir plata");
			comentarios.addComentarios("i have been studying english since janaury");
			return comentarios;
		});
		
		Mono<UsuarioComentarios> usrConComentarios = usrMono
				.zipWith(comentariosUsrMono, (usr, comentariosUsr) -> {
			return new UsuarioComentarios(usr, comentariosUsr);
		});
		usrConComentarios.subscribe(uc -> LOG.info(uc.toString()));
	}
	
	public void ejemploUsuarioComentariosZipWithForma2() {
		
		Mono<Usuario> usrMono = Mono.fromCallable(() -> new Usuario("Juan", "Soancil"));
		
		Mono<Comentario> comentariosUsrMono = Mono.fromCallable(() -> {
			Comentario comentarios = new Comentario();
			comentarios.addComentarios("Hola sapa");
			comentarios.addComentarios("camilo se forro");
			comentarios.addComentarios("debemos conseguir plata");
			comentarios.addComentarios("i have been studying english since janaury");
			return comentarios;
		});
		
		Mono<UsuarioComentarios> usrConComentarios = usrMono
				.zipWith(comentariosUsrMono)
				.map(tuple -> {
					Usuario u = tuple.getT1();
					Comentario c = tuple.getT2();
					return new UsuarioComentarios(u, c);
				});
		usrConComentarios.subscribe(uc -> LOG.info(uc.toString()));
	}
	
	public void ejemploZipWithRangos() {
		
		Flux<Integer> rango = Flux.range(1, 5);
		Flux
			.just(1,2,3,4,5)
			.map(i -> i*2)
			.zipWith(rango, (seq1, seq2) -> 
				String.format("Primer Flux %d, Segundo Flux %d", seq1, seq2)
			)
			.subscribe(flux -> LOG.info(flux));
	}
	
	public void ejemploZipWithRangosDelay() {
		
		Flux<Integer> rango = Flux.range(1, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));
		
		rango
			.zipWith(retraso, (r, d) -> r)
			.doOnNext(i -> LOG.info(i.toString()))
//			.subscribe(); 
			.blockLast(); // No recomendable hacer esto porque le quita lo asincrono
	}
	
	public void ejemploDelayElements() {
		Flux<Integer> rango = Flux.range(1, 12)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> LOG.info(i.toString()));
		rango//.subscribe(); 
		.blockLast();// No recomendable hacer esto porque le quita lo asincrono		
	}
	
	// metodo importante 
	public void ejemploIntervalInfinite() throws InterruptedException {
		
		CountDownLatch latch = new CountDownLatch(1);
		
		Flux
			.interval(Duration.ofSeconds(1))
			.doOnTerminate(() -> latch.countDown())
			.flatMap( i -> {
				if(i >= 5) return Flux.error(new InterruptedException("Only until 5th!"));
				return Flux.just(i);
			})
			.map(i -> "Hola " + i)
//			.retry(2) // si ocurre un error intenta 2 veces
			.subscribe(s -> LOG.info(s), e -> LOG.error(e.getMessage()));
		
		latch.await();
	}
	
	public void ejemploIntervalDesdeCreate () {
		Flux
			.create(emitter -> {
				
				Timer timer = new Timer();
				
				timer.schedule(new TimerTask() {
					
					private Integer count = 0;
					
					@Override
					public void run() {
						emitter.next(++count);
						if (count == 10) {
							timer.cancel();
							emitter.complete();
						}
						
						if(count == 5) {
							timer.cancel();
							emitter.error(new InterruptedException("Error. se ha detenido el flux en 5!"));
						}
					}
					
				}, 1000, 1000);
				
			})
			.subscribe(
				next -> LOG.info(next.toString()),
				error -> LOG.error(error.getMessage()),
				() -> LOG.info("Hemos terminado!")
			);
	}
	
	// metodo muy importante para manejar la contrapesion de sobrecarga de informacion
	public void ejemploContraPresion() {
		Flux
			.range(1, 10)
			.log()
			.subscribe(new Subscriber<Integer>() {

				private Subscription s;
				private Integer limit = 5;
				private Integer consumido = 0;
				
				@Override
				public void onSubscribe(Subscription s) {
					this.s = s;
					s.request(limit);
				}

				@Override
				public void onNext(Integer t) {
					LOG.info(t.toString());
					consumido++;
					if (consumido == limit) { 
						consumido = 0; 
						s.request(limit);
					} 
				}

				@Override
				public void onError(Throwable t) {
				}

				@Override
				public void onComplete() {
				}
			});
	}
	
	// metodo muy importante para manejar la contrapesion de sobrecarga de informacion, manera elegante
		public void ejemploContraPresion2() {
			Flux
				.range(1, 10)
				.log()
				.limitRate(5)
				.subscribe();
		}
}
