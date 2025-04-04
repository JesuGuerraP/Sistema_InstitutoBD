// Funciones y eventos específicos para la vista de crear finanza
document.addEventListener('DOMContentLoaded', function () {
    const createButton = document.getElementById('createButton');

    // Verificar si el botón existe
    if (!createButton) {
        console.error('No se encontró el botón con id "createButton". Verifica el HTML.');
        return; // Detenemos la ejecución si no existe el botón
    }

    // Añadir el evento click al botón
    createButton.addEventListener('click', function () {
        console.log('Botón de guardar clickeado');

        // Obtener los datos del formulario
        const data = {
            alumnoId: document.getElementById('alumnoId').value,
            pagoInscripcion: parseFloat(document.getElementById('pagoInscripcion').value) || 0,
            descuento: parseFloat(document.getElementById('descuento').value) || 0,
            pago1: parseFloat(document.getElementById('pago1').value) || 0,
            pago2: parseFloat(document.getElementById('pago2').value) || 0,
            pago3: parseFloat(document.getElementById('pago3').value) || 0,
            pago4: parseFloat(document.getElementById('pago4').value) || 0,
            pago5: parseFloat(document.getElementById('pago5').value) || 0,
            fechaCreacion: document.getElementById('fechaCreacion').value // Obtenemos la fecha de creación
        };

        // Validación básica en el frontend
        if (!data.alumnoId) {
            alert('Debe seleccionar un alumno.');
            return;
        }

        // Enviar los datos al backend
        fetch('/finanzas/save', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.ok) {
                    console.log('Finanza creada exitosamente');
                    window.location.href = '/finanzas'; // Redirigir a la vista de finanzas
                } else {
                    console.error('Error al guardar la finanza');
                    alert('Error al guardar la finanza');
                }
            })
            .catch(error => {
                console.error('Error en la solicitud:', error);
                alert('Error al guardar la finanza');
            });
    });
});
