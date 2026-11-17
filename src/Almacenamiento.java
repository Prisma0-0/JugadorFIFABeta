import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// para que guarde los datos y los cargue
public class Almacenamiento {

    // para que se cree en la carpeta raiz
    private static final String ARCHIVO = "datos_fifa.csv";
    private static final String SEP = ";";

    // Evita que el archivo se dañe
    private static String limpiar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace(SEP, ",").replace("\n", " ").trim();
    }

    //se encarga del guardado
    public static void guardar(ArrayList<Jugador> jugadores, ArrayList<Equipo> equipos) {
        List<String> lineas = new ArrayList<>();

        // asigna una linea a los jugadores
        for (Jugador j : jugadores) {
            String nacimiento = (j.getNacimiento() == null) ? "" : j.getNacimiento().toString();
            String posicion = (j.posicion == null) ? "" : j.posicion.name();
            lineas.add("JUGADOR" + SEP + limpiar(j.name)
                    + SEP + limpiar(j.getCountry())
                    + SEP + limpiar(j.getId())
                    + SEP + j.dorsal
                    + SEP + posicion
                    + SEP + nacimiento);
        }

        // asigna una linea a cada equipo
        for (Equipo e : equipos) {
            Tecnico t = e.getTecnico();
            String nombreTecnico = (t == null) ? "" : limpiar(t.name);
            lineas.add("EQUIPO" + SEP + limpiar(e.getTeamName()) + SEP + nombreTecnico);
        }

        // lo mismo que las anteriores pero para jugadores en equipos
        for (Equipo e : equipos) {
            for (Jugador j : e.getPlantilla()) {
                lineas.add("MIEMBRO" + SEP + limpiar(e.getTeamName()) + SEP + limpiar(j.getId()));
            }
        }

        try {
            Files.write(Paths.get(ARCHIVO), lineas, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.println("No se pudo guardar el archivo: " + ex.getMessage());
        }
    }

    //Carga lo guardado
    public static void cargar(ArrayList<Jugador> jugadores, ArrayList<Equipo> equipos) {
        Path ruta = Paths.get(ARCHIVO);

        if (!Files.exists(ruta)) {
            return;
        }

        List<String> lineas;
        try {
            lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            System.out.println("No se pudo leer el archivo: " + ex.getMessage());
            return;
        }

        for (String linea : lineas) {
            if (linea.trim().isEmpty()) {
                continue;
            }
            //el -1 es para que no se pierdan los campos vacios
            String[] p = linea.split(SEP, -1);

            try {
                switch (p[0]) {
                    case "JUGADOR": {
                        LocalDate nacimiento = p[6].isEmpty() ? null : LocalDate.parse(p[6]);
                        Jugador j = new Jugador(p[1], nacimiento);
                        j.setCountry(p[2]);
                        j.setId(p[3]);
                        j.dorsal = Integer.parseInt(p[4]);
                        if (!p[5].isEmpty()) {
                            j.posicion = Posicion.valueOf(p[5]);
                        }
                        jugadores.add(j);
                        break;
                    }
                    case "EQUIPO": {
                        Equipo e = new Equipo(p[1]);
                        if (!p[2].isEmpty()) {
                            Tecnico t = new Tecnico();
                            t.name = p[2];
                            t.club = p[1];
                            e.setTecnico(t);
                        }
                        equipos.add(e);
                        break;
                    }
                    case "MIEMBRO": {
                        Equipo e = buscarEquipo(equipos, p[1]);
                        Jugador j = buscarJugador(jugadores, p[2]);
                        if (e != null && j != null) {
                            e.agregarJugador(j);
                        }
                        break;
                    }
                    default:
                        break;
                }
            } catch (RuntimeException ex) {
                // Si una linea se daña la saltara
                System.out.println("Linea ignorada por error de formato: " + linea);
            }
        }

        System.out.println("Datos cargados: " + jugadores.size() + " jugadores y "
                + equipos.size() + " equipos.");
    }

    // Auxiliares
    private static Equipo buscarEquipo(ArrayList<Equipo> equipos, String nombre) {
        for (Equipo e : equipos) {
            if (e.getTeamName().equals(nombre)) {
                return e;
            }
        }
        return null;
    }

    private static Jugador buscarJugador(ArrayList<Jugador> jugadores, String id) {
        for (Jugador j : jugadores) {
            if (id.equals(j.getId())) {
                return j;
            }
        }
        return null;
    }
}