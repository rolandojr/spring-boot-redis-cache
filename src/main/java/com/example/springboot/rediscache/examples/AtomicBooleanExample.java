package com.example.springboot.rediscache.examples;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanExample {
    private static final AtomicBoolean disconnected = new AtomicBoolean(false);

    public static void main(String[] args) throws InterruptedException {
        int threads = 8;

        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int id = i;
            workers[i] = new Thread(() -> {
                // Cada hilo intenta simular el "shutdown" solo si puede cambiar de false -> true
                if (disconnected.compareAndSet(false, true)) {
                    System.out.println("Hilo " + id + " realizó la desconexión (compareAndSet devolvió true).");
                    // Simula la tarea de shutdown
                    try {
                        Thread.sleep(200); // trabajo simulado
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    System.out.println("Hilo " + id + " comprobó que ya estaba desconectado (compareAndSet devolvió false).");
                }
            }, "worker-" + i);
            workers[i].start();
        }

        for (Thread t : workers) {
            t.join();
        }

        System.out.println("Valor final de disconnected: " + disconnected.get());
    }
}
