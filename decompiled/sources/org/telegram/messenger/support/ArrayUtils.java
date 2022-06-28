package org.telegram.messenger.support;

import java.lang.reflect.Array;
/* loaded from: classes4.dex */
public class ArrayUtils {
    private static Object[] EMPTY = new Object[0];
    private static final int CACHE_SIZE = 73;
    private static Object[] sCache = new Object[CACHE_SIZE];

    private ArrayUtils() {
    }

    public static int idealByteArraySize(int need) {
        for (int i = 4; i < 32; i++) {
            if (need <= (1 << i) - 12) {
                return (1 << i) - 12;
            }
        }
        return need;
    }

    public static int idealBooleanArraySize(int need) {
        return idealByteArraySize(need);
    }

    public static int idealShortArraySize(int need) {
        return idealByteArraySize(need * 2) / 2;
    }

    public static int idealCharArraySize(int need) {
        return idealByteArraySize(need * 2) / 2;
    }

    public static int idealIntArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealFloatArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealObjectArraySize(int need) {
        return idealByteArraySize(need * 4) / 4;
    }

    public static int idealLongArraySize(int need) {
        return idealByteArraySize(need * 8) / 8;
    }

    public static boolean equals(byte[] array1, byte[] array2, int length) {
        if (array1 == array2) {
            return true;
        }
        if (array1 == null || array2 == null || array1.length < length || array2.length < length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }

    public static <T> T[] emptyArray(Class<T> kind) {
        if (kind == Object.class) {
            return (T[]) EMPTY;
        }
        int bucket = ((System.identityHashCode(kind) / 8) & Integer.MAX_VALUE) % CACHE_SIZE;
        Object cache = sCache[bucket];
        if (cache == null || cache.getClass().getComponentType() != kind) {
            cache = Array.newInstance((Class<?>) kind, 0);
            sCache[bucket] = cache;
        }
        return (T[]) ((Object[]) cache);
    }

    public static <T> boolean contains(T[] array, T value) {
        for (T element : array) {
            if (element == null) {
                if (value == null) {
                    return true;
                }
            } else if (value != null && element.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    public static int indexOf(int[] array, int value) {
        if (array != null) {
            for (int a = 0; a < array.length; a++) {
                if (array[a] == value) {
                    return a;
                }
            }
            return -1;
        }
        return -1;
    }

    public static long total(long[] array) {
        long total = 0;
        for (long value : array) {
            total += value;
        }
        return total;
    }

    public static <T> T[] appendElement(Class<T> kind, T[] array, T element) {
        T[] result;
        int end;
        if (array != null) {
            end = array.length;
            result = (T[]) ((Object[]) Array.newInstance((Class<?>) kind, end + 1));
            System.arraycopy(array, 0, result, 0, end);
        } else {
            end = 0;
            result = (T[]) ((Object[]) Array.newInstance((Class<?>) kind, 1));
        }
        result[end] = element;
        return result;
    }

    public static <T> T[] removeElement(Class<T> kind, T[] array, T element) {
        if (array != null) {
            int length = array.length;
            for (int i = 0; i < length; i++) {
                if (array[i] == element) {
                    if (length == 1) {
                        return null;
                    } else {
                        T[] result = (T[]) ((Object[]) Array.newInstance((Class<?>) kind, length - 1));
                        System.arraycopy(array, 0, result, 0, i);
                        System.arraycopy(array, i + 1, result, i, (length - i) - 1);
                        return result;
                    }
                }
            }
        }
        return array;
    }

    public static int[] appendInt(int[] cur, int val) {
        if (cur == null) {
            return new int[]{val};
        }
        int N = cur.length;
        for (int i : cur) {
            if (i == val) {
                return cur;
            }
        }
        int i2 = N + 1;
        int[] ret = new int[i2];
        System.arraycopy(cur, 0, ret, 0, N);
        ret[N] = val;
        return ret;
    }

    public static int[] removeInt(int[] cur, int val) {
        if (cur == null) {
            return null;
        }
        int N = cur.length;
        for (int i = 0; i < N; i++) {
            if (cur[i] == val) {
                int[] ret = new int[N - 1];
                if (i > 0) {
                    System.arraycopy(cur, 0, ret, 0, i);
                }
                if (i < N - 1) {
                    System.arraycopy(cur, i + 1, ret, i, (N - i) - 1);
                }
                return ret;
            }
        }
        return cur;
    }
}
