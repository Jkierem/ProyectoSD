# ProyectoSD

## Paquetes:

Los siguientes paquetes estan definidos:
- authentication: Todo lo referente a autenticacion. 
- client: Todo lo referente al cliente y la interfaz grafica
  - views: Paquete de vistas donde estan las vistas de la interfaz grafica
- interfaces: Interfaces remotas compartidas
- store: todo lo referente al manejo de productos
- shared: Conjunto de funcionalidades utilitarias que no hacen parte del negocio
  - logic : funciones que contienen logica que puede ser compartida entre paquetes
  - functional : implementaciones funcionales
  - utils : funciones utilitarias
  - exceptions: exceptiones personalizadas para transmitir llamados mal formados

## Flujo esperado de Cliente:

- [X] 1 - Autenticar
- [X] 2 - Armar Pedido
- [X] 3 - Realizar Compra

El control de concurrencia que se debe usar es optimista con chequeo foward.

### 1 - Autenticar

El usuario se autentica usando el numero de tarjeta y una contraseña. El cliente se encarga de mandar un hash en SHA256 al servidor de autenticacion.

La autenticacion es exitosa si:
- El usuario esta ingresando por primera vez
- El usuario ingresa bien sus credenciales

La autenticacion falla si:
- El servidor no esta corriendo
- El usuario ingresa un numero de tarjeta inexistente
- El usuario ingresa mal sus credenciales

El usuario es representado en disco como una tupla de la forma: 

[userId],[balance],[contraseña],[isAdmin]

En memoria es represantado como un mapa de parejas llave-valor de la forma:

userId - UserData

UserData:
- password: String
- balance : int
- isAdmin : boolean

### 2 - Armar Pedido

- [X] Control de concurrencia
- [X] 2PC

#### 2.1 - Manejo de balances

- [X] Validacion de monto
- [X] Funcion para realizar 2PC (canCommitBalanceChange)
- [X] Uso de token administrador
- [X] Usuario administrador
- [X] Integracion con manejo de transacciones de producto

#### 2.2 - Manejo de recursos

