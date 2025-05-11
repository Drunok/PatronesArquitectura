import java.util.*;

// Creamos un singleton que representa el almacen de nuestra cafeteria
class Stock {
    private static Stock instance;
    public String nombre;
    public ArrayList<String> almacen;

    // ponemos en privado el constructor de stock para evitar que sea instanciado por aqui
    private Stock(String nombre, String[] productos) {
        this.nombre = nombre;
        this.almacen = new ArrayList<>();
        for (String producto : productos) {
            almacen.add(producto);
        }
    }

    public static Stock getInstance(String nombre, String[] almacen) {
        if (instance == null) {
            instance = new Stock(nombre, almacen);
        }
        return instance;
    }

    // metodos para actuar sobre los elementos de nuestro stock
    public void agregarProducto(String producto) {
        almacen.add(producto);
        Notificador.getInstance().notificar("Producto agregado: " + producto);
    }

    public void quitarProducto(String producto) {
        if (almacen.remove(producto)) {
            Notificador.getInstance().notificar("Producto eliminado: " + producto);
        } else {
            Notificador.getInstance().notificar("Producto no encontrado: " + producto);
        }
    }
}

// La siguiente permite avisar a múltiples observadores cuando algo cambia
// como agregar o quitar productos del stock.
interface Observador {
    void actualizar(String mensaje);
}

class Notificador {
    private static Notificador instance;
    private List<Observador> observadores = new ArrayList<>();

    private Notificador() {}

    public static Notificador getInstance() {
        if (instance == null) {
            instance = new Notificador();
        }
        return instance;
    }

    public void agregarObservador(Observador o) {
        observadores.add(o);
    }
    public void notificar(String mensaje) {
        for (Observador o : observadores) {
            o.actualizar(mensaje);
        }
    }
}

class ObservadorConsola implements Observador {
    public void actualizar(String mensaje) {
        System.out.println("Notificación: " + mensaje);
    }
}

// Usamos el patron de estructura de Facade para que podamos simplificar el uso de Stock y Notificator
// desde afuera 
class StockFacade {
    private Stock stock;

    public StockFacade(String nombre, String[] productos) {
        stock = Stock.getInstance(nombre, productos);
    }

    public void agregar(String producto) {
        stock.agregarProducto(producto);
    }

    public void quitar(String producto) {
        stock.quitarProducto(producto);
    }

    public void mostrarAlmacen() {
        System.out.println("Almacén de " + stock.nombre + ": " + stock.almacen);
    }
}



public class Cafeteria {
    public static void main(String[] args) {
        // Creamos un observador que escucha los cambios en el stock
        ObservadorConsola observador = new ObservadorConsola();
        Notificador.getInstance().agregarObservador(observador);

        // Creamos el sistema de stock con algunos productos iniciales
        String[] productosIniciales = {"Cafe", "Leche", "Galletas"};
        StockFacade stockFacade = new StockFacade("Almacén Central", productosIniciales);

        // Acciones sobre el stock
        stockFacade.agregar("Crema");
        stockFacade.agregar("Azucar");
        stockFacade.quitar("Leche");
        stockFacade.quitar("Torta"); // No existe

        // Mostrar el contenido final del almacén
        stockFacade.mostrarAlmacen();
    }
}
