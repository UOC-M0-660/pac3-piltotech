# PARTE TEORICA

### Lifecycle

#### Explica el ciclo de vida de una Activity.

##### ¿Por qué vinculamos las tareas de red a los componentes UI de la aplicación?
Las conexiones a la red para descargar o subir datos son tareas que no dependen del dispositivo sino de la carga de la red o la cobertura móvil que dispongamos, de forma que pueden ejecutarse de forma prácticamente inmediata, sin que el usuario note ningún retraso, o tardar varios segundos en completarse. Estas tareas deben comunicarse con la interfaz gráfica de la aplicación para informar de que se está descargando datos remotos, o que se está enviando información, pero, en cualquier caso, no debería bloquear la interfaz, lo cual puede dar una mala experiencia de usuario o sensación de errores en la app. En nuestro ejercicio se han usado la herramienta coroutine que nos da la posibilidad de ejecutar tareas en segundo plano, como autentificar al usuario, durante lo cual nuestra aplicación sigue "viva" mostrando un icono de progreso.

##### ¿Qué pasaría si intentamos actualizar la recyclerview con nuevos streams después de que el usuario haya cerrado la aplicación?
En nuestro ejercicio se ha usado corutines con un scope Main para la interfaz y un scope IO para las tareas de red en las diferentes activities de la aplicación. Cuando la aplicación se cierra las corutines también quedan canceladas. Por lo que, en este caso, si cerramos la aplicación mientras estamos descargando datos, la corrutina se cancela y nunca llegaríamos a actualizar la recycler view al acabar de recibir los datos.

##### Describe brevemente los principales estados del ciclo de vida de una Activity.
Las actividades son los componentes para interacción con el usuario de las aplicaciones. Podemos distinguir entre cuatro estados:
En ejecución (running): Es cuando la actividad esta visible, en primer plano, y el usuario puede interactuar con los componentes de la actividad.
Pausada (paused): Es cuando la actividad es visible para el usuario, en ocasiones solo parcialmente, pero el usuario no puede interactuar con ella.
Detenida (stopped): Es cuando la actividad ya no es visible para el usuario, y por tanto tampoco se puede interactuar, pero se mantiene en la memoria del dispositivo.
Inexistente: Es cuando finalmente la aplicación termina y se libera la memoria en el dispositivo.
Al pasar por los diferentes estados se ejecutan varias funciones:
onCreate-onStart-onResume: para pasar desde inexistente a ejecución (pasando por los estados intermedios)
onPause-onStop-onDestroy: al terminar la aplicación desde running (pasando por los estados intermedios)
---

### Paginación 

#### Explica el uso de paginación en la API de Twitch.

##### ¿Qué ventajas ofrece la paginación a la aplicación?
La paginación que nos ofrece la API de twitch al pedir los streams nos permite obtener los resultados de una gran lista agrupados en paquetes de un numero fijado de streams. Esta funcionalidad nos permite que nuestra aplicación sea más rápida a la hora de cargar los resultados de la petición a la web ya que no debe descargar toda la información, sino ir descargando pequeñas porciones. El número de streams se puede configurar con el objetivo de ofrecer la mejor experiencia al usuario, haciendo las mínimas peticiones posibles al servidor, y pidiendo la cantidad justa de elementos o información para no saturar al usuario. Además, en dispositivos móviles, esta funcionalidad se adapta perfectamente a aplicaciones como el ejercicio de ejemplo, y con el simple gesto de desplazarnos por los resultados deslizando el dedo la información se va actualizando en un scroll infinito.

##### ¿Qué problemas puede tener la aplicación si no se utiliza paginación?
El principal problema si no usamos paginación es la cantidad de información que deberíamos descargar y el tiempo antes de mostrarla al usuario. En el ejemplo de twitch, si pidiéramos la lista completa tardaría un gran tiempo durante el cual la aplicación no mostraría resultados, además de ocupar un gran espacio de memoria.

##### Lista algunos ejemplos de aplicaciones que usan paginación.
Consultando la documentación de diferentes APIs podemos comprobar el uso de la paginación está bastante extendido y que la gran mayoría requieren el uso de esta funcionalidad cuando accedemos a sus recursos. Como ejemplos de APIs que requieren paginación podemos nombrar Twitter, Facebook, Youtube, Ebay, Flickr, Pinterest y por tanto, sus aplicaciones oficiales como otras que accedan a través de su API usaran paginación.
