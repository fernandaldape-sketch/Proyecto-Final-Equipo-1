import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Los seis métodos de ordenamiento implementados sobre ArrayList<Integer> (con get/set) */
public class Lista {

    public static boolean estaOrdenado(List<Integer> l) {
        for (int i = 1; i < l.size(); i++) if (l.get(i - 1) > l.get(i)) return false;
        return true;
    }

    public static void burbuja(List<Integer> a) {
        int n = a.size();
        for (int i = 0; i < n - 1; i++) {
            boolean cambio = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (a.get(j) > a.get(j + 1)) { swap(a, j, j + 1); cambio = true; }
            }
            if (!cambio) break;
        }
    }

    public static void seleccion(List<Integer> a) {
        int n = a.size();
        for (int i = 0; i < n - 1; i++) {
            int min = i;
            for (int j = i + 1; j < n; j++) if (a.get(j) < a.get(min)) min = j;
            if (min != i) swap(a, i, min);
        }
    }

    public static void insercion(List<Integer> a) {
        for (int i = 1; i < a.size(); i++) {
            int clave = a.get(i), j = i - 1;
            while (j >= 0 && a.get(j) > clave) { a.set(j + 1, a.get(j)); j--; }
            a.set(j + 1, clave);
        }
    }

    public static void merge(List<Integer> a) {
        if (a.size() < 2) return;
        List<Integer> tmp = new ArrayList<>(Collections.nCopies(a.size(), 0));
        mergeRec(a, tmp, 0, a.size() - 1);
    }

    private static void mergeRec(List<Integer> a, List<Integer> tmp, int lo, int hi) {
        if (lo >= hi) return;
        int mid = (lo + hi) >>> 1;
        mergeRec(a, tmp, lo, mid);
        mergeRec(a, tmp, mid + 1, hi);
        int i = lo, j = mid + 1, k = lo;
        while (i <= mid && j <= hi) {
            if (a.get(i) <= a.get(j)) tmp.set(k++, a.get(i++));
            else tmp.set(k++, a.get(j++));
        }
        while (i <= mid) tmp.set(k++, a.get(i++));
        while (j <= hi) tmp.set(k++, a.get(j++));
        for (int x = lo; x <= hi; x++) a.set(x, tmp.get(x));
    }

    public static void quick(List<Integer> a) { quickRec(a, 0, a.size() - 1); }

    private static void quickRec(List<Integer> a, int lo, int hi) {
        while (lo < hi) {
            int p = a.get(lo + (hi - lo) / 2);
            int lt = lo, i = lo, gt = hi;
            while (i <= gt) {
                int v = a.get(i);
                if (v < p) swap(a, lt++, i++);
                else if (v > p) swap(a, i, gt--);
                else i++;
            }
            if (lt - lo < hi - gt) { quickRec(a, lo, lt - 1); lo = gt + 1; }
            else { quickRec(a, gt + 1, hi); hi = lt - 1; }
        }
    }

    public static void heap(List<Integer> a) {
        int n = a.size();
        for (int i = n / 2 - 1; i >= 0; i--) heapify(a, n, i);
        for (int i = n - 1; i > 0; i--) { swap(a, 0, i); heapify(a, i, 0); }
    }

    private static void heapify(List<Integer> a, int n, int i) {
        while (true) {
            int mayor = i, l = 2 * i + 1, r = 2 * i + 2;
            if (l < n && a.get(l) > a.get(mayor)) mayor = l;
            if (r < n && a.get(r) > a.get(mayor)) mayor = r;
            if (mayor == i) return;
            swap(a, i, mayor);
            i = mayor;
        }
    }

    private static void swap(List<Integer> a, int i, int j) {
        int t = a.get(i);
        a.set(i, a.get(j));
        a.set(j, t);
    }
}