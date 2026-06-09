# Juego Matriz — Neo vs Agentes

**Nombre:** Samuel Gil  
**Materia:** Arquitecturas de Software (ARSW)  
**Institución:** Escuela Colombiana de Ingeniería

Simulación de un juego en una grilla NxN donde **Neo** debe llegar a un teléfono antes de que los **Agentes** lo capturen. Cada entidad se mueve de forma autónoma usando el algoritmo de camino mínimo BFS, ejecutándose en hilos virtuales de Java 21.

---

## De qué trata

La grilla contiene cuatro tipos de entidades:

| Símbolo | Entidad | Descripción |
|---------|---------|-------------|
| Círculo azul | **Neo** | Protagonista. Se mueve automáticamente hacia el teléfono más cercano usando BFS. |
| Triángulo rojo | **Agente** | Enemigo inteligente. Persigue a Neo usando BFS. Puede haber varios. |
| Rectángulo gris | **Obstáculo** | Bloquea el movimiento de todos. Posición fija. |
| Círculo verde | **Teléfono** | Objetivo de Neo. Posición fija. |

**Condiciones de victoria:**

- **Neo gana** si llega a una casilla con teléfono.
- **Los Agentes ganan** si un Agente ocupa la misma casilla que Neo.

Si un Agente o Neo queda completamente rodeado de obstáculos sin camino posible, simplemente se queda quieto.

---

## Cómo se usan los hilos

El proyecto aplica el modelo de **concurrencia estructurada con hilos virtuales** de Java 21, siguiendo el mismo patrón del LAB1 (Snake Race).

### Un hilo por entidad 

```
Neo       →  NeoRunner   (virtual thread)
Agente 1  →  AgentRunner (virtual thread)
Agente 2  →  AgentRunner (virtual thread)

```

Cada hilo corre en bucle infinito mientras el juego esté en estado `RUNNING`:

```
mientras juego en RUNNING:
    1. Verificar si está pausado    
    2. Calcular próxima casilla   
    3. Mover en la matriz         
    4. Esperar un tick            
```

### PauseControl — monitor wait/notifyAll

Cuando el usuario presiona **Pausar**, todos los hilos activos se bloquean en `checkPause()` mediante el mecanismo `wait()` de Java. Al reanudar, `notifyAll()` los despierta a todos simultáneamente.

```java

public synchronized void checkPause() throws InterruptedException {
    while (paused) wait();
}


public synchronized void resume() {
    paused = false;
    notifyAll();
}
```

### Sincronización de la matriz

`Matrix` es el estado compartido entre todos los hilos. Sus métodos `moveNeo()` y `moveAgent()` están sincronizados para evitar condiciones de carrera al leer o escribir posiciones. La UI nunca modifica el estado: solo lee un **snapshot inmutable** via `matrix.snapshot()`.

### GameClock — repintado independiente

Un `ScheduledExecutorService` dispara el repintado de la ventana a ~60 fps independientemente del ritmo de los hilos de juego. Esto mantiene la UI fluida aunque el juego corra lento.

```
GameClock (60 fps)  →  SwingUtilities.invokeLater(repaint)
NeoRunner           →  tick cada 600 ms
AgentRunner         →  tick cada 800 ms
```

---

## Requisitos

- Java 21 o superior
- Maven 3.8+

---

## Cómo ejecutar

```bash
git clone https://github.com/samuel-gil-r/ARSW-matriz.git
cd ARSW-matriz
mvn exec:java
```

El programa pedirá los parámetros por consola:

```
Ingrese el tamano de la matriz (N): 15
Ingrese el numero de Agentes (minimo 1): 3
Ingrese el numero de Obstaculos: 20
Ingrese el numero de Telefonos (minimo 1): 2
```

Tras confirmar, se abre la ventana gráfica y el juego comienza automáticamente.

---

## Cómo jugar

El juego corre solo — no hay control manual. El rol del usuario es **observar** el resultado de la simulación:

1. **Observar** cómo Neo (azul) calcula su ruta hacia el teléfono más cercano.
2. **Observar** cómo los Agentes (rojo) lo persiguen simultáneamente.
3. Presionar **Pausar** en cualquier momento para congelar todos los hilos.
4. Presionar **Reanudar** para continuar.
5. El juego termina solo cuando hay un ganador y muestra un mensaje emergente.



        
```
