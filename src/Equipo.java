import java.util.ArrayList;

public class Equipo {
    // Encapsulado para que solo se pueda cambiar con los metodos de abajo
    private String teamName;
    private Tecnico tecnico_equipo;
    private ArrayList<Jugador> plantilla = new ArrayList<>();

    public Equipo(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Tecnico getTecnico() {
        return tecnico_equipo;
    }

    public void setTecnico(Tecnico tecnico) {
        this.tecnico_equipo = tecnico;
    }

    // Devuelve una COPIA de la lista, asi nadie de afuera
    // puede modificar la plantilla sin pasar por agregar/eliminar
    public ArrayList<Jugador> getPlantilla() {
        return new ArrayList<>(plantilla);
    }

    public boolean contieneJugador(Jugador j) {
        return plantilla.contains(j);
    }

    // true si lo agrego, false si no pudo o ya estaba
    public boolean agregarJugador(Jugador j) {
        if (j == null || plantilla.contains(j)) {
            return false;
        }
        plantilla.add(j);
        return true;
    }

    // true si lo elimino, false si no estaba
    public boolean eliminarJugador(Jugador j) {
        return plantilla.remove(j);
    }

    @Override
    public String toString() {
        String tec = (tecnico_equipo == null) ? "Sin tecnico" : tecnico_equipo.name;
        return "Equipo{" +
                "teamName='" + teamName + '\'' +
                ", tecnico=" + tec +
                ", jugadores=" + plantilla.size() +
                '}';
    }
}