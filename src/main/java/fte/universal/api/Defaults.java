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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import fte.http.Page;

/**
 * @author Try-Parser
 *
 */
public interface Defaults<T extends Serializable> {
	public Optional<UUID> uuidParsing(JsonNode id);
	
	public @ResponseBody List<?> get();
	
	public @ResponseBody Page<?> page(@PathVariable(value="first") final Integer first, @PathVariable(value="max") final Integer max);
	
	public @ResponseBody ResponseEntity<?> get(@Valid @RequestBody JsonNode body, Errors error);
	
	public @ResponseBody ResponseEntity<?> delete(@Valid @RequestBody JsonNode body, Errors error);
	
	public @ResponseBody ResponseEntity<?> saveOrUpdate(@Valid @RequestBody T t, Errors error);
}
