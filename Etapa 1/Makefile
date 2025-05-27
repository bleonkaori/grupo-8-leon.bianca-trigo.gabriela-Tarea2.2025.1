
JAVAC = javac #convertir .java a .class
JAVA = java #esto para ejecutar
SRC = *.java # compilar todos los archivos .java
MAIN = Stage1 #clase principal que tiene el main


#aqui cambiar segun donde esté descargado Javafx
JAVA_FX_PATH = C:\\Users\\Bianca\\Downloads\\javafx-sdk-21.0.7\\lib


#modulos que se usa para Javafx
FX_OPTS = --module-path $(JAVA_FX_PATH) --add-modules javafx.controls,javafx.fxml

#por defecto si escribe make
all: compile

#instruccion de compilado
compile:
	$(JAVAC) $(FX_OPTS) $(SRC)

#instruccion de ejecutar
run: compile
	$(JAVA) $(FX_OPTS) $(MAIN)

#limpiar archivos .class
clean:
	rm -f *.class
