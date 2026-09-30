/** Los seis métodos de ordenamiento implementados sobre arreglos int[] */
public class Arreglo {

    public static boolean estaOrdenado(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i - 1] > a[i]) return false;
        return true;
    }

    public static void burbuja(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            boolean cambio = false;
            for (int j = 0; j < a.length - 1 - i; j++) {
                if (a[j] > a[j + 1]) { swap(a, j, j + 1); cambio = true; }
            }
            if (!cambio) break;
        }
    }

    public static void seleccion(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++) if (a[j] < a[min]) min = j;
            if (min != i) swap(a, i, min);
        }
    }

    public static void insercion(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int clave = a[i], j = i - 1;
            while (j >= 0 && a[j] > clave) { a[j + 1] = a[j]; j--; }
            a[j + 1] = clave;
        }
    }

    public static void merge(int[] a) {
        if (a.length < 2) return;
        mergeRec(a, new int[a.length], 0, a.length - 1);
    }

    private static void mergeRec(int[] a, int[] tmp, int lo, int hi) {
        if (lo >= hi) return;
        int mid = (lo + hi) >>> 1;
        mergeRec(a, tmp, lo, mid);
        mergeRec(a, tmp, mid + 1, hi);
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) tmp[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];
        while (i <= mid) tmp[k++] = a[i++];
        while (j <= hi) tmp[k++] = a[j++];
        System.arraycopy(tmp, lo, a, lo, hi - lo + 1);
    }

    public static void quick(int[] a) { quickRec(a, 0, a.length - 1); }

    // Partición en 3 vías: evita el peor caso con muchos valores repetidos (ej. 1 a 5)
    private static void quickRec(int[] a, int lo, int hi) {
        while (lo < hi) {
            int p = a[lo + (hi - lo) / 2];
            int lt = lo, i = lo, gt = hi;
            while (i <= gt) {
                if (a[i] < p) swap(a, lt++, i++);
                else if (a[i] > p) swap(a, i, gt--);
                else i++;
            }
            // Se recurre en el lado menor para limitar la profundidad de la pila
            if (lt - lo < hi - gt) { quickRec(a, lo, lt - 1); lo = gt + 1; }
            else { quickRec(a, gt + 1, hi); hi = lt - 1; }
        }
    }

    public static void heap(int[] a) {
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--) heapify(a, n, i);
        for (int i = n - 1; i > 0; i--) { swap(a, 0, i); heapify(a, i, 0); }
    }

    private static void heapify(int[] a, int n, int i) {
        while (true) {
            int mayor = i, l = 2 * i + 1, r = 2 * i + 2;
            if (l < n && a[l] > a[mayor]) mayor = l;
            if (r < n && a[r] > a[mayor]) mayor = r;
            if (mayor == i) return;
            swap(a, i, mayor);
            i = mayor;
        }
    }

    private static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }
}