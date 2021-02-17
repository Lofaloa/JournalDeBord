package journal.de.bord.api.drivers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

/**
 * The controller handle the REST interface exposing the drivers resources.
 */
@RestController
public class DriverController {

    private static final String DRIVERS_RESOURCE_PATH = "/api/drivers";
    private static final String DRIVER_RESOURCE_PATH = "/api/drivers/{identifier}";

    @Autowired
    private DriverDatabaseTable driverDatabaseTable;

    /**
     * Creates a new driver.
     *
     * @param driver is the pseudonym of the driver to create a new location
     * for.
     * @return the response without content (created status, 201).
     * @throws ResponseStatusException when the driver identifier already
     * exists (409). Or when the provided data results in a null pointer
     * exception (422).
     */
    @PostMapping(path = DRIVERS_RESOURCE_PATH)
    public ResponseEntity create(@Valid @RequestBody DriverDto driver) {
        try {
            driverDatabaseTable.create(driver);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Gets a specific driver.
     *
     * @param identifier is the identifier of the driver to get.
     * @return the response containing the location.
     * @throws ResponseStatusException 404 when the specified driver identifier
     * could not be found.
     */
    @GetMapping(path = DRIVER_RESOURCE_PATH)
    public ResponseEntity driver(@PathVariable("identifier") String identifier) {
        try {
            return ResponseEntity.ok(driverDatabaseTable.findById(identifier));
        } catch (NullPointerException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    /**
     * Gets all the drivers.
     *
     * @return the response containing a list of locations.
     */
    @GetMapping(path = DRIVERS_RESOURCE_PATH)
    public ResponseEntity drivers() {
        return ResponseEntity.ok(driverDatabaseTable.findAll());
    }

    /**
     * Replaces the specified location with the given one.
     *
     * @param identifier is the driver id.
     * @return the response without content (204).
     * @throws ResponseStatusException when the identifier or the driver is
     * unknown (404). Or when the body contains a valid driver with a name
     * that already exist (409). Or when there is a mismatch between the URI
     * identifier and the data identifier.
     */
    @PutMapping(path = DRIVER_RESOURCE_PATH)
    public ResponseEntity update(
            @PathVariable("identifier") String identifier,
            @Valid @RequestBody DriverDto data
    ) {
        try {
            if (!identifier.equals(data.getIdentifier())) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
            }
            driverDatabaseTable.update(data);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Deletes the specified driver.
     *
     * @param identifier is the driver id.
     * @return the response without content (204).
     * @throws ResponseStatusException if the driver or the location cannot be
     * found (404). Or when the location is referenced by one of the driver's
     * stop (409).
     */
    @DeleteMapping(path = DRIVER_RESOURCE_PATH)
    public ResponseEntity delete(@PathVariable("identifier") String identifier) {
        try {
            driverDatabaseTable.deleteById(identifier);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}