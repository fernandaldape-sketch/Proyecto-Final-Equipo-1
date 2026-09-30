import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Main {

    record Resultado(String nombre, String estructura, double tiempoMs, boolean ok) {
    }

    interface OrdenaArreglo { void ordenar(int[] a); }
    interface OrdenaLista { void ordenar(List<Integer> l); }

    static final OrdenaArreglo[] ARR = {
        OrdenamientoArreglo::burbuja, OrdenamientoArreglo::seleccion, OrdenamientoArreglo::insercion,
        OrdenamientoArreglo::merge, OrdenamientoArreglo::quick, OrdenamientoArreglo::heap
    };
    static final OrdenaLista[] LST = {
        OrdenamientoLista::burbuja, OrdenamientoLista::seleccion, OrdenamientoLista::insercion,
        OrdenamientoLista::merge, OrdenamientoLista::quick, OrdenamientoLista::heap
    };

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String seguir;
        do {
            System.out.print("¿Cuántos elementos vas a ordenar? ");
            int n = leerEntero(sc);
            System.out.print("¿Valores restringidos entre 1 y 5? (s/n): ");
            boolean restringido = sc.next().trim().equalsIgnoreCase("s");

            Random rnd = new Random();
            int[] original = new int[n];
            for (int i = 0; i < n; i++) {
                original[i] = restringido ? rnd.nextInt(5) + 1 : rnd.nextInt(1_000_000);
            }

            ConcurrentHashMap<String, Resultado> resultados = new ConcurrentHashMap<>();
            CountDownLatch salida = new CountDownLatch(1);
            List<Thread> hilos = new ArrayList<>();

            for (int k = 0; k < Reporte.NOMBRES.length; k++) {
                final String nombre = Reporte.NOMBRES[k];

                final int[] copiaArr = original.clone();
                final OrdenaArreglo oa = ARR[k];
                hilos.add(new Thread(() -> {
                    esperar(salida);
                    long t0 = System.nanoTime();
                    oa.ordenar(copiaArr);
                    long t1 = System.nanoTime();
                    boolean ok = OrdenamientoArreglo.estaOrdenado(copiaArr);
                    resultados.put(nombre + "|Arreglo",
                        new Resultado(nombre, "Arreglo", (t1 - t0) / 1_000_000.0, ok));
                }, nombre + "-Arreglo"));

                final List<Integer> copiaLst = new ArrayList<>(n);
                for (int v : original) copiaLst.add(v);
                final OrdenaLista ol = LST[k];
                hilos.add(new Thread(() -> {
                    esperar(salida);
                    long t0 = System.nanoTime();
                    ol.ordenar(copiaLst);
                    long t1 = System.nanoTime();
                    boolean ok = OrdenamientoLista.estaOrdenado(copiaLst);
                    resultados.put(nombre + "|ArrayList",
                        new Resultado(nombre, "ArrayList", (t1 - t0) / 1_000_000.0, ok));
                }, nombre + "-ArrayList"));
            }

            System.out.println("\nEjecutando 12 hilos... (los métodos O(n^2) pueden tardar con n grande)");
            for (Thread t : hilos) t.start();
            salida.countDown();
            for (Thread t : hilos) t.join();

            Reporte.imprimir(resultados.values(), n, restringido);

            System.out.print("\n¿Repetir con otra cantidad? (s/n): ");
            seguir = sc.next();
        } while (seguir.trim().equalsIgnoreCase("s"));
    }

    static int leerEntero(Scanner sc) {
        while (true) {
            if (sc.hasNextInt()) {
                int n = sc.nextInt();
                if (n > 0) return n;
            } else {
                sc.next();
            }
            System.out.print("Introduce un entero positivo: ");
        }
    }

    static void esperar(CountDownLatch l) {
        try { l.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    static class OrdenamientoArreglo {
        static void burbuja(int[] a) {
            int n = a.length;
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    if (a[j] > a[j + 1]) {
                        int aux = a[j];
                        a[j] = a[j + 1];
                        a[j + 1] = aux;
                    }
                }
            }
        }

        static void seleccion(int[] a) {
            int n = a.length;
            for (int i = 0; i < n - 1; i++) {
                int min = i;
                for (int j = i + 1; j < n; j++) {
                    if (a[j] < a[min]) min = j;
                }
                if (min != i) {
                    int aux = a[i];
                    a[i] = a[min];
                    a[min] = aux;
                }
            }
        }

        static void insercion(int[] a) {
            for (int i = 1; i < a.length; i++) {
                int key = a[i];
                int j = i - 1;
                while (j >= 0 && a[j] > key) {
                    a[j + 1] = a[j];
                    j--;
                }
                a[j + 1] = key;
            }
        }

        static void merge(int[] a) {
            if (a.length < 2) return;
            mergeSort(a, 0, a.length - 1);
        }

        private static void mergeSort(int[] a, int left, int right) {
            if (left >= right) return;
            int mid = left + (right - left) / 2;
            mergeSort(a, left, mid);
            mergeSort(a, mid + 1, right);
            merge(a, left, mid, right);
        }

        private static void merge(int[] a, int left, int mid, int right) {
            int[] aux = new int[right - left + 1];
            int i = left, j = mid + 1, k = 0;
            while (i <= mid && j <= right) {
                if (a[i] <= a[j]) aux[k++] = a[i++];
                else aux[k++] = a[j++];
            }
            while (i <= mid) aux[k++] = a[i++];
            while (j <= right) aux[k++] = a[j++];
            for (int p = 0; p < aux.length; p++) {
                a[left + p] = aux[p];
            }
        }

        static void quick(int[] a) {
            if (a.length < 2) return;
            quickSort(a, 0, a.length - 1);
        }

        private static void quickSort(int[] a, int left, int right) {
            if (left >= right) return;
            int pivot = a[left + (right - left) / 2];
            int i = left, j = right;
            while (i <= j) {
                while (a[i] < pivot) i++;
                while (a[j] > pivot) j--;
                if (i <= j) {
                    int aux = a[i];
                    a[i] = a[j];
                    a[j] = aux;
                    i++;
                    j--;
                }
            }
            if (left < j) quickSort(a, left, j);
            if (i < right) quickSort(a, i, right);
        }

        static void heap(int[] a) {
            int n = a.length;
            for (int i = n / 2 - 1; i >= 0; i--) {
                heapify(a, n, i);
            }
            for (int i = n - 1; i > 0; i--) {
                int aux = a[0];
                a[0] = a[i];
                a[i] = aux;
                heapify(a, i, 0);
            }
        }

        private static void heapify(int[] a, int n, int i) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < n && a[left] > a[largest]) largest = left;
            if (right < n && a[right] > a[largest]) largest = right;
            if (largest != i) {
                int aux = a[i];
                a[i] = a[largest];
                a[largest] = aux;
                heapify(a, n, largest);
            }
        }

        static boolean estaOrdenado(int[] a) {
            for (int i = 1; i < a.length; i++) {
                if (a[i - 1] > a[i]) return false;
            }
            return true;
        }
    }

    static class OrdenamientoLista {
        static void burbuja(List<Integer> l) {
            int n = l.size();
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - i - 1; j++) {
                    if (l.get(j) > l.get(j + 1)) {
                        int aux = l.get(j);
                        l.set(j, l.get(j + 1));
                        l.set(j + 1, aux);
                    }
                }
            }
        }

        static void seleccion(List<Integer> l) {
            int n = l.size();
            for (int i = 0; i < n - 1; i++) {
                int min = i;
                for (int j = i + 1; j < n; j++) {
                    if (l.get(j) < l.get(min)) min = j;
                }
                if (min != i) {
                    int aux = l.get(i);
                    l.set(i, l.get(min));
                    l.set(min, aux);
                }
            }
        }

        static void insercion(List<Integer> l) {
            for (int i = 1; i < l.size(); i++) {
                int key = l.get(i);
                int j = i - 1;
                while (j >= 0 && l.get(j) > key) {
                    l.set(j + 1, l.get(j));
                    j--;
                }
                l.set(j + 1, key);
            }
        }

        static void merge(List<Integer> l) {
            if (l.size() < 2) return;
            Integer[] arr = l.toArray(Integer[]::new);
            mergeSort(arr, 0, arr.length - 1);
            for (int i = 0; i < arr.length; i++) {
                l.set(i, arr[i]);
            }
        }

        private static void mergeSort(Integer[] arr, int left, int right) {
            if (left >= right) return;
            int mid = left + (right - left) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }

        private static void merge(Integer[] arr, int left, int mid, int right) {
            Integer[] aux = new Integer[right - left + 1];
            int i = left, j = mid + 1, k = 0;
            while (i <= mid && j <= right) {
                if (arr[i] <= arr[j]) aux[k++] = arr[i++];
                else aux[k++] = arr[j++];
            }
            while (i <= mid) aux[k++] = arr[i++];
            while (j <= right) aux[k++] = arr[j++];
            for (int p = 0; p < aux.length; p++) {
                arr[left + p] = aux[p];
            }
        }

        static void quick(List<Integer> l) {
            if (l.size() < 2) return;
            Integer[] arr = l.toArray(Integer[]::new);
            quickSort(arr, 0, arr.length - 1);
            for (int i = 0; i < arr.length; i++) {
                l.set(i, arr[i]);
            }
        }

        private static void quickSort(Integer[] arr, int left, int right) {
            if (left >= right) return;
            int pivot = arr[left + (right - left) / 2];
            int i = left, j = right;
            while (i <= j) {
                while (arr[i] < pivot) i++;
                while (arr[j] > pivot) j--;
                if (i <= j) {
                    int aux = arr[i];
                    arr[i] = arr[j];
                    arr[j] = aux;
                    i++;
                    j--;
                }
            }
            if (left < j) quickSort(arr, left, j);
            if (i < right) quickSort(arr, i, right);
        }

        static void heap(List<Integer> l) {
            Integer[] arr = l.toArray(Integer[]::new);
            int n = arr.length;
            for (int i = n / 2 - 1; i >= 0; i--) {
                heapify(arr, n, i);
            }
            for (int i = n - 1; i > 0; i--) {
                int aux = arr[0];
                arr[0] = arr[i];
                arr[i] = aux;
                heapify(arr, i, 0);
            }
            for (int i = 0; i < arr.length; i++) {
                l.set(i, arr[i]);
            }
        }

        private static void heapify(Integer[] arr, int n, int i) {
            int largest = i;
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            if (left < n && arr[left] > arr[largest]) largest = left;
            if (right < n && arr[right] > arr[largest]) largest = right;
            if (largest != i) {
                int aux = arr[i];
                arr[i] = arr[largest];
                arr[largest] = aux;
                heapify(arr, n, largest);
            }
        }

        static boolean estaOrdenado(List<Integer> l) {
            for (int i = 1; i < l.size(); i++) {
                if (l.get(i - 1) > l.get(i)) return false;
            }
            return true;
        }
    }

    static class Reporte {
        static final String[] NOMBRES = {
            "Burbuja", "Seleccion", "Insercion", "Merge", "Quick", "Heap"
        };

        static void imprimir(java.util.Collection<Resultado> resultados, int n, boolean restringido) {
            List<Resultado> lista = new ArrayList<>(resultados);
            lista.sort(Comparator.comparing(Resultado::nombre).thenComparing(Resultado::estructura));

            System.out.println("\n    Resultado final");
            System.out.println("Elementos: " + n + " | Valores restringidos: " + (restringido ? "si" : "no"));
            System.out.printf("%-14s %-12s %-10s %-6s%n", "Algoritmo", "Estructura", "Tiempo(ms)", "OK");
            for (Resultado r : lista) {
                System.out.printf("%-14s %-12s %-10.3f %-6s%n",
                    r.nombre(), r.estructura(), r.tiempoMs(), r.ok() ? "OK" : "NO");
            }
        }
    }
}