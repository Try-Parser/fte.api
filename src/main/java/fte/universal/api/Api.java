/**
 * 
 */
package fte.universal.api;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import fte.http.Page;
import fte.http.Response;
import fte.universal.Crud;

/**
 * @author Try-Parser
 *
 */
public abstract class Api<T extends Crud<C, I>, C extends Serializable, I extends Serializable> implements Defaults<C> {

	public @Autowired T service;

	@Override
	@GetMapping("/")
	public @ResponseBody List<C> get() {
		return service.get();
	}
	
	@Override
	@GetMapping("/{first}/{max}")
	public @ResponseBody Page<C> page(@PathVariable(value="first") final Integer first, @PathVariable(value="max") final Integer max) {
		return service.paginate(first, max);
	}

	@Override
	@PostMapping("/")
	public @ResponseBody ResponseEntity<?> get(@Valid @RequestBody JsonNode body, Errors error) {
		if(error.hasErrors()) 
			return ResponseEntity.badRequest().body(error.getAllErrors());
				
		return uuidParsing(body.get("id")).map(uid -> {
			try {
				@SuppressWarnings("unchecked")
				Optional<C> response = service.get((I) uid);
				return (response.isPresent()) ?
					 ResponseEntity.ok().body(response.get()) : 
					 ResponseEntity.badRequest().body("{ status: 404, message: Data not found. }");
			} catch(Exception e) {
				return ResponseEntity.badRequest().body("{ status: 500, message: Bad data. }");
			}	
		}).orElseGet(() -> {
			return ResponseEntity.badRequest().body("{ status: 500, message: Bad data. }");
		});
	}
	
	@Override
	@PatchMapping("/")
	public @ResponseBody ResponseEntity<?> saveOrUpdate(@Valid @RequestBody C t, Errors error) {
		if(error.hasErrors()) 
			return ResponseEntity.badRequest().body(error.getAllErrors());
		
		try {
			List<Response> response = service.saveOrUpdate(t);
			return ResponseEntity.ok().body(response);
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("{ status: 500, message: bad data. }");
		}
	}
	
	@Override
	@DeleteMapping("/")
	public @ResponseBody ResponseEntity<?> delete(@Valid @RequestBody JsonNode body, Errors error) {
		if(error.hasErrors())
			return ResponseEntity.badRequest().body(error.getAllErrors());
		
		System.out.println(body.get("id"));
		
		return uuidParsing(body.get("id")).map(uid -> {
			try {
				@SuppressWarnings("unchecked")
				List<Response> response = service.disable(true, (I) uid);
				System.out.print(response);
				return ResponseEntity.ok().body(response);
			} catch(Exception e) {
				e.printStackTrace();
				return ResponseEntity.badRequest().body("{ status: 500, message: bad data. }");
			}
		}).orElseGet(() -> {
			return ResponseEntity.badRequest().body("{ status: 500, message: Bad data. }");
		});
	}

	@Override
	public Optional<UUID> uuidParsing(JsonNode id) {
		try {
			return Optional.of(UUID.fromString(id.asText()));
		} catch(Exception e) {
			return Optional.empty();
		}
	}
}
