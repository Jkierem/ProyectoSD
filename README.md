# ProyectoSD

## Paquetes:

Los siguientes paquetes estan definidos:
- authentication: Todo lo referente a autenticacion. 
- client: Todo lo referente al cliente
- interfaces: Interfaces remotas compartidas
- models: Modelos de datos
- shared: Conjunto de funcionalidades utilitarias que no hacen parte del negocio
- shared.logic : funciones que contienen logica que puede ser compartida entre paquetes
- shared.functional : implementaciones funcionales
- shared.utils : funciones utilitarias

## Flujo esperado de Cliente:

- [ ] 1 - Autenticar
- [ ] 2 - Armar Pedido
- [ ] 3 - Realizar Compra

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

### 2 - Armar Pedido
### 3 - Realizar Compra
