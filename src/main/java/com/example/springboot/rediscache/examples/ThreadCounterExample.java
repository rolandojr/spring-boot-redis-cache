package com.example.springboot.rediscache.examples;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase de ejemplo para mostrar tres formas de incrementar un contador desde varios hilos:
 *  - no atómico (posibles condiciones de carrera)
 *  - usando synchronized (bloqueo)
 *  - usando AtomicInteger (operación atómica, recomendada para este caso)
 */
public class ThreadCounterExample {

    private final AtomicInteger atomicCounter = new AtomicInteger(0);
    private int nonAtomicCounter = 0;
    private final Object lock = new Object();

    public void incrementAtomic() {
        atomicCounter.incrementAndGet();
    }

    public void incrementSynchronized() {
        synchronized (lock) {
            nonAtomicCounter++;
        }
    }

    public void incrementNonAtomic() {
        // Sin sincronización: susceptible a condiciones de carrera
        nonAtomicCounter++;
    }

    public int getAtomic() {
        return atomicCounter.get();
    }

    public int getNonAtomic() {
        synchronized (lock) {
            return nonAtomicCounter;
        }
    }

    public void resetNonAtomic() {
        synchronized (lock) {
            nonAtomicCounter = 0;
        }
    }

    private static void runTest(String name, int threads, int incrementsPerThread, Runnable incrementAction) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    incrementAction.run();
                }
                latch.countDown();
            });
            t.start();
        }

        latch.await();
        System.out.printf("Test %s finalizado.%n", name);
    }

    public void runAtomicTest(int threads, int incrementsPerThread) throws InterruptedException {
        runTest("AtomicInteger", threads, incrementsPerThread, this::incrementAtomic);
        System.out.printf("Atomic expected=%d actual=%d%n", threads * incrementsPerThread, getAtomic());
    }

    public void runNonAtomicTest(int threads, int incrementsPerThread) throws InterruptedException {
        resetNonAtomic();
        runTest("NonAtomic (no sync)", threads, incrementsPerThread, this::incrementNonAtomic);
        System.out.printf("NonAtomic expected=%d actual=%d%n", threads * incrementsPerThread, getNonAtomic());
    }

    public void runSynchronizedTest(int threads, int incrementsPerThread) throws InterruptedException {
        resetNonAtomic();
        runTest("Synchronized", threads, incrementsPerThread, this::incrementSynchronized);
        System.out.printf("Synchronized expected=%d actual=%d%n", threads * incrementsPerThread, getNonAtomic());
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadCounterExample example = new ThreadCounterExample();

        int threads = 100;
        int incrementsPerThread = 1000;

        System.out.println("Ejecutando prueba AtomicInteger...");
        example.runAtomicTest(threads, incrementsPerThread);

        System.out.println();
        System.out.println("Ejecutando prueba Non-Atomic (esperar discrepancias)...");
        example.runNonAtomicTest(threads, incrementsPerThread);

        System.out.println();
        System.out.println("Ejecutando prueba Synchronized...");
        example.runSynchronizedTest(threads, incrementsPerThread);
    }
}

