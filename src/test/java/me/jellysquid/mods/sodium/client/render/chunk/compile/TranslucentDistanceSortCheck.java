package me.jellysquid.mods.sodium.client.render.chunk.compile;

import it.unimi.dsi.fastutil.ints.IntArrays;
import java.util.Arrays;
import java.util.Random;

public final class TranslucentDistanceSortCheck {
    private static volatile int sink;

    public static void main(String[] args) {
        Random random = new Random(0x617274);
        int checks = 0;
        for (int size : new int[]{0,1,63,1023,4095,4096,4097,8192,32768}) {
            for (int kind = 0; kind < 8; kind++) {
                for (int repeat = 0; repeat < 12; repeat++) {
                    float[] distances = new float[size];
                    for (int i = 0; i < size; i++) {
                        distances[i] = switch (kind) {
                            case 0 -> random.nextFloat() * 50000;
                            case 1 -> random.nextInt(32);
                            case 2 -> 1;
                            case 3 -> i;
                            case 4 -> size - i;
                            case 5 -> i % 2 == 0 ? -0.0f : 0.0f;
                            case 6 -> switch(i % 4) {case 0 -> Float.POSITIVE_INFINITY; case 1 -> Float.NEGATIVE_INFINITY; default -> random.nextFloat() * -100;};
                            default -> Float.intBitsToFloat(random.nextInt());
                        };
                    }
                    int[] expected = indices(size);
                    IntArrays.mergeSort(expected,(a,b)->Float.compare(distances[b],distances[a]));
                    int[] actual = TranslucentDistanceSort.sort(distances);
                    if(!Arrays.equals(expected,actual))throw new AssertionError("Sort differs: size="+size+" kind="+kind);
                    checks++;
                }
            }
        }
        System.out.println(checks + " exact stable-order cases passed, including threshold edges, ties, signed zero, infinities and NaNs.");
        for(int size:new int[]{1024,4096,8192,32768}) {
            float[] distances=new float[size];
            for(int i=0;i<size;i++)distances[i]=random.nextInt(10000);
            for(int i=0;i<300;i++){measure(distances,false);measure(distances,true);}
            long[] old=new long[120],candidate=new long[120];
            for(int i=0;i<old.length;i++) {
                if(i%2==0){old[i]=measure(distances,false);candidate[i]=measure(distances,true);}
                else{candidate[i]=measure(distances,true);old[i]=measure(distances,false);}
            }
            Arrays.sort(old);Arrays.sort(candidate);
            System.out.printf("%d quads: merge %.3f ms, candidate %.3f ms, ratio %.3f%n",size,old[60]/1e6,candidate[60]/1e6,(double)candidate[60]/old[60]);
        }
    }

    private static long measure(float[] distances,boolean candidate) {
        long start=System.nanoTime();
        int[] indices;
        if(candidate)indices=TranslucentDistanceSort.sort(distances);
        else {indices=indices(distances.length);IntArrays.mergeSort(indices,(a,b)->Float.compare(distances[b],distances[a]));}
        long elapsed=System.nanoTime()-start;
        sink=indices[indices.length/2];
        return elapsed;
    }

    private static int[] indices(int size) {
        int[] out=new int[size];for(int i=0;i<size;i++)out[i]=i;return out;
    }
}
