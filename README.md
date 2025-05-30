
# 📘 README

## ✅ Prerrequisitos
Antes de comenzar, asegúrate de tener lo siguiente instalado y configurado en tu sistema:

- Java (versión 21 o la que use tu proyecto)
- Maven
- Variables de entorno (PATH) configuradas para java y mvn

Verifica con:
```
java -version
mvn -version
```

---

## 🗂️ Etapa 1: Ejecutar aplicación JavaFX

1. Abrir la terminal de IntelliJ en el ícono de abajo a la izquierda, para poder así commpilar los archivos de las etapas 1,2,3 y 4, mediante comandos mvn
   ```
   cd "Etapa 1"
   ```
2. Ejecutar la aplicación:
   ```
   mvn javafx:run
   ```
3. Limpiar el proyecto:
   ```
   mvn clean
   ```
4. Volver a la carpeta raíz:
   ```
   cd ..
   ```

---

## 🗂️ Etapa 2: Ejecutar aplicación JavaFX

1. Ingresar a la carpeta:
   ```
   cd "Etapa 2"
   ```
2. Ejecutar:
   ```
   mvn javafx:run
   ```
3. Limpiar:
   ```
   mvn clean
   ```
4. Regresar:
   ```
   cd ..
   ```

---

## 🗂️ Etapa 3: Ejecutar aplicación JavaFX

1. Ingresar a la carpeta:
   ```
   cd "Etapa 3"
   ```
2. Ejecutar:
   ```
   mvn javafx:run
   ```
3. Limpiar:
   ```
   mvn clean
   ```
4. Regresar:
   ```
   cd ..
   ```

---

## 🗂️ Etapa 4: Generar documentación (Javadoc)

1. Ingresar a la carpeta:
   ```
   cd "Etapa 4"
   ```

2. Generar documentación:
   ```
   mvn javadoc:javadoc
   ```
   > Esto creará una carpeta `target/site/apidocs/` dentro de la etapa.  
   > Para ver la documentación, abre el archivo 
`index.html` haciendo doble clic sobre él.

3. Limpiar:
   ```
   mvn clean
   ```

4. Volver:
   ```
   cd ..
   ```

## ✅ Conclusión

Con estos pasos ejecutaste correctamente las etapas del proyecto.  
Si ocurre algún error, asegúrate de limpiar usando el comando "mvn clean" y repetir la ejecución.
