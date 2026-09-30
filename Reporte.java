import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/** Imprime la tabla comparativa, la más rápida/lenta y la complejidad Big O */
public class Reporte {

    static final String[] NOMBRES = {"Burbuja", "Seleccion", "Insercion", "Merge Sort", "Quick Sort", "Heap Sort"};

    static final String[] BIG_O = {
        "Mejor O(n) | Prom O(n^2) | Peor O(n^2)",
        "Mejor O(n^2) | Prom O(n^2) | Peor O(n^2)",
        "Mejor O(n) | Prom O(n^2) | Peor O(n^2)",
        "Mejor O(n log n) | Prom O(n log n) | Peor O(n log n)",
        "Mejor O(n log n) | Prom O(n log n) | Peor O(n^2)*",
        "Mejor O(n log n) | Prom O(n log n) | Peor O(n log n)"
    };

    public static void imprimir(Collection<Resultado> resultados, int n, boolean restringido) {
        List<Resultado> lista = new ArrayList<>(resultados);
        lista.sort(Comparator.comparingDouble(Resultado::ms));

        System.out.println("\nRESULTADOS DE ORDENAMIENTO");
        System.out.println("Elementos: " + n + (restringido ? " (valores 1-5)" : " (aleatorios)"));
        System.out.printf("%n%-5s %-12s %-12s %-14s %-8s%n", "Pos.", "Algoritmo", "Estructura", "Tiempo (ms)", "¿Ordenó?");
        int pos = 1;
        for (var r : lista) {
            System.out.printf("%-5d %-12s %-12s %-14.3f %-8s%n",
                pos++, r.algoritmo(), r.estructura(), r.ms(), r.ordeno() ? "Sí" : "No");
        }

        Resultado mejor = lista.get(0), peor = lista.get(lista.size() - 1);
        System.out.println("\nImplementación con menor tiempo registrado: " + mejor.algoritmo() + " (" + mejor.estructura() + ")");
        System.out.println("Implementación con mayor tiempo registrado: " + peor.algoritmo() + " (" + peor.estructura() + ")");

        System.out.println("\nCOMPLEJIDAD TEÓRICA (Big O)");
        for (int i = 0; i < NOMBRES.length; i++) {
            System.out.printf("%-12s %s%n", NOMBRES[i], BIG_O[i]);
        }
        System.out.println("* Quick Sort: el peor caso O(n^2) depende del pivote; aquí se usa pivote central y partición en 3 vías.");
        System.out.println("Nota: los tiempos varían porque los 12 hilos comparten CPU; una sola medición no es concluyente.");
    }
}