import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    //lleva al panel los cambios
    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Jugador> jugadores = new ArrayList<>();
    private static final ArrayList<Equipo> equipos = new ArrayList<>();

    public static void main(String[] args) {
        Almacenamiento.cargar(jugadores, equipos);

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Que, va a hacer manito?");
            switch (opcion){
                case 1:
                    crearJugador();
                    break;
                case 2:
                    crearEquipo();
                    break;
                case 3:
                    agregarJugadorAEquipo();
                    break;
                case 4:
                    eliminarJugadorDeEquipo();
                    break;
                case 5:
                    verEquipo();
                    break;
                case 6:
                    verTodosLosJugadores();
                    break;
                case 7:
                    verTodosLosEquipos();
                    break;
                case 0:
                    System.out.println("Se me cuida manito");
                    break;
                default:
                    System.out.println("Trateme serio socio");
            }
            Almacenamiento.guardar(jugadores, equipos);

        } while (opcion != 0);
    }

    private static void mostrarMenu() {
        System.out.println("*****MENU*****");
        System.out.println("1. Crear Juegador");
        System.out.println("2. Crear Equipazo");
        System.out.println("3. Agregar Juegador");
        System.out.println("4. Sacar Juegador de un Equipo (por inuti xd)");
        System.out.println("5. Ver Informacion de un equipo");
        System.out.println("6. Ver Todos Los Juegadores");
        System.out.println("7. Ver Todos los Equipos");
        System.out.println("0. Salir");
    }

    //haciendo que el menu menunee
    private static void crearJugador() {
        System.out.println("CREAR JUGADOR");
        String nombre = leerTexto("Nombre: ");
        String pais = leerTexto("Pais: ");

        //Pa no repetir id
        String id;
        while (true) {
            id = leerTexto("ID: ");
            if (idExiste(id)) {
                System.out.println("ya existe un jugador con ese id");
            } else{
                break;
            }
        }

        int dorsal = leerDorsal();
        Posicion posicion = leerPosicion();
        LocalDate nacimiento = leerFexha();

        //encapsulados :v
        Jugador j = new Jugador(nombre, nacimiento);
        j.setCountry(pais);
        j.setId(id);
        j.dorsal = dorsal;
        j.posicion = posicion;

        jugadores.add(j);
        System.out.println("Juegador creado: " + nombre);
    }

    private static void crearEquipo() {
        System.out.println("CREAR EQUIPAZO");
        String nombre = leerTexto("Nombre del equipo: ");

        for (Equipo e : equipos) {
            if (e.getTeamName().equalsIgnoreCase(nombre)){
                System.out.println("Paila un equipo ya tiene ese nombre :v");
                return;
            }
        }

        Equipo equipo = new Equipo(nombre);

        System.out.println("Nombre del tecnico (si desea no usar tecnico pulse enter): ");
        String nombreTecnico = sc.nextLine().trim();
        if(!nombreTecnico.isEmpty()) {
            Tecnico t = new Tecnico();
            t.name = nombreTecnico;
            t.club = nombre;
            equipo.setTecnico(t);
        }

        equipos.add(equipo);
        System.out.println("Equipo creado: " + nombre);
    }

    private static void agregarJugadorAEquipo(){
        System.out.println("AGREGAR JUEGADOR A UN EQUIPAZO");
        if (jugadores.isEmpty()){
            System.out.println("Primero crea juegadores en la opcion 1");
            return;
        }
        Equipo equipo = seleccionarEquipo();
        if (equipo == null) {
            return;
        }

        Jugador j = seleccionarJugador(jugadores, "Jugadores registrados:");

        //Para que un juegador no este en mas de 2 equipos
        Equipo actual = buscarEquipoDe(j);
        if (actual != null) {
            System.out.println(j.name + "ya esta en " + actual.getTeamName()
                       + ".si desea agregarlo a un nuevo equipo puedes eliminarlo del actual (opcion 4)");
            return;
        }

        if (equipo.agregarJugador(j)) {
            System.out.println(j.name + "fue agregado a " + equipo.getTeamName());
        } else {
            System.out.println("no se pudo agregar el jugador");
        }
    }

    private static void eliminarJugadorDeEquipo() {
        System.out.println("HECHAR UN INUTIL xD");
        Equipo equipo = seleccionarEquipo();
        if (equipo == null) {
            return;
        }
        ArrayList<Jugador> plantilla= equipo.getPlantilla();
        if (plantilla.isEmpty()) {
            System.out.println(equipo.getTeamName() + "no tiene Juegadores");
            return;
        }
        Jugador j = seleccionarJugador(plantilla, "Plantilla de " + equipo.getTeamName() + ":");
        if (j == null) {
            return;
        }
        if (equipo.eliminarJugador(j)) {
            System.out.println(j.name + " fue eliminado de " + equipo.getTeamName());
        } else {
            System.out.println("No se pudo eliminar el juegador");
        }
    }

    private static void verEquipo() {
        System.out.println("INFORME DE EQUIPO");
        Equipo e = seleccionarEquipo();
        if (e == null) {
            return;
        }
        System.out.println("Equipo: " + e.getTeamName());
        Tecnico t = e.getTecnico();
        System.out.println("Tecnico: " + (t == null ? "Sin tecnico" : t.name));

        ArrayList<Jugador> plantilla = e.getPlantilla();
        System.out.println("Jugadores (" + plantilla.size() + "):");
        if (plantilla.isEmpty()) {
            System.out.println(" (ninguno aun)");
        } else {
            for (Jugador j : plantilla) {
                System.out.println(" - " + describirJugador(j));
            }
        }
    }

    private static void verTodosLosJugadores() {
        System.out.println("TODOS LOS JUEGADORES");
        if (jugadores.isEmpty()) {
            System.out.println("No hay juegadores creados");
            return;
        }
        for (int i = 0; i < jugadores.size(); i++) {
            Jugador j = jugadores.get(i);
            Equipo e = buscarEquipoDe(j);
            System.out.println((i + 1) + ". " + describirJugador(j)
            + " |Equipo: " + (e == null ? "Sin equipo" : e.getTeamName()));
        }
    }

    private static void verTodosLosEquipos() {
        System.out.println("TODOS LOS EQUIPAZOS");
        if (equipos.isEmpty()) {
            System.out.println("No hay equipos aun");
            return;
        }
        for (int i = 0; i < equipos.size(); i++) {
            System.out.println((i+1) + ". " + equipos.get(i));
        }
    }

    //linea de texto con los datos de cada juegador
    private static String describirJugador(Jugador j) {
        return j.name
                + " | dorsal " + j.dorsal
                + " | " + j.posicion
                + " | pais: " + j.getCountry()
                + " | edad: " + j.getEdad()
                + " | ID: " + j.getId();
    }

    private static boolean idExiste(String id) {
        for (Jugador j : jugadores) {
            if (id.equals(j.getId())) {
                return true;
            }
        }
        return false;
    }

    //busca el equipo del juegador
    private static Equipo buscarEquipoDe(Jugador j) {
        for (Equipo e : equipos) {
            if (e.contieneJugador(j)) {
                return e;
            }
        }
        return null;
    }

    //Para que elijan equipo
    private static Equipo seleccionarEquipo() {
        if (equipos.isEmpty()) {
            System.out.println("Primero crea equipos en la opcion 2");
            return null;
        }
        System.out.println("Equipos: ");
        for (int i = 0; i < equipos.size(); i++) {
            System.out.println((i + 1) + ". " + equipos.get(i).getTeamName());
        }
        int n = leerEntero("Numero del equipo: ");
        if (n < 1 || n > equipos.size()) {
            System.out.println("Numero invalido");
            return null;
        }
        return equipos.get(n - 1);
    }

    //Para elegir un jugador
    private static Jugador seleccionarJugador(ArrayList<Jugador> lista, String titulo) {
        System.out.println(titulo);
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + describirJugador(lista.get(i)));
        }
        int n = leerEntero("Numero del juegador:");
        if (n < 1 || n > lista.size()) {
            System.out.println("Numero invalido");
            return null;
        }
        return lista.get(n - 1);
    }

    private static int leerDorsal() {
        while (true) {
            int d = leerEntero("Dorsal (1-99): ");
            if (d >= 1 && d <= 99) {
                return d;
            }
            System.out.println("el dorsal debe estar entre 1 y 99");
        }
    }

    private static Posicion leerPosicion() {
        Posicion[] todas = Posicion.values();
        System.out.println("Posiciones:");
        for (int i = 0; i < todas.length; i++) {
            System.out.println((i + 1) + ". " + todas[i]);
        }
        while (true) {
            int n = leerEntero("Elija la posicion: ");
            if (n >= 1 && n <= todas.length) {
                return todas[n - 1];
            }
            System.out.println("Numero invalido.");
        }
    }

    private static LocalDate leerFexha() {
        while (true) {
            String texto = leerTexto("Fecha de nacimiento (Año-mes-dia) en numeros: ");
            try {
                LocalDate f = LocalDate.parse(texto);
                if (f.isAfter(LocalDate.now())) {
                    System.out.println("Su juagador nacion en el futuro? payaso");
                }else {
                    return f;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Formato invalido. solo use numeros en la forma Año-Mes-Dia");
            }
        }
    }

    //no premite vacios
    private static String leerTexto(String mensaje) {
        while (true) {
            System.out.println(mensaje);
            String texto = sc.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("No puede estar vacio");
        }
    }

    //Para que no tiren el programa con un entero
    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.println(mensaje);
            String linea = sc.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Escribe un numero entero valido");
            }
        }
    }
}




















