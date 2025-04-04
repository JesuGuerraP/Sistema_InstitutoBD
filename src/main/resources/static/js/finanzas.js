document.addEventListener('DOMContentLoaded', function () {
    var detailsModal = document.getElementById('detailsModal');
    
    detailsModal.addEventListener('show.bs.modal', function (event) {
        var button = event.relatedTarget;
        var finanzaId = button.getAttribute('data-bs-id');
        
        // Realizar llamada AJAX para obtener los detalles
        fetch(`/detalles/${finanzaId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error al obtener los detalles de la finanza');
                }
                return response.json();
            })
            .then(finanza => {
                // Actualizar los campos del modal con los datos recibidos
                document.getElementById('finanzaId').textContent = finanza.id;
                document.getElementById('finanzaAlumno').textContent = finanza.alumnoNombres;
                document.getElementById('finanzaPagoInscripcion').textContent = finanza.pagoInscripcion;
                document.getElementById('finanzaDescuento').textContent = finanza.descuento + '%';
                document.getElementById('finanzaValorSemestre').textContent = finanza.valorSemestre;
                document.getElementById('finanzaTotalPagadoSemestre').textContent = finanza.totalPagadoSemestre;
                document.getElementById('finanzaDeudaTotalSemestre').textContent = finanza.deudaTotalSemestre;
                document.getElementById('finanzaEstado').textContent = finanza.estado;
                document.getElementById('finanzaFechaCreacion').textContent = finanza.fechaCreacion;
                document.getElementById('finanzaFechaActualizacion').textContent = finanza.fechaActualizacion;
                
                // Actualizar los pagos si existen
                document.getElementById('finanzaPago1').textContent = finanza.pago1 || 'No registrado';
                document.getElementById('finanzaPago2').textContent = finanza.pago2 || 'No registrado';
                document.getElementById('finanzaPago3').textContent = finanza.pago3 || 'No registrado';
                document.getElementById('finanzaPago4').textContent = finanza.pago4 || 'No registrado';
                document.getElementById('finanzaPago5').textContent = finanza.pago5 || 'No registrado';
            })
            .catch(error => {
                console.error('Error:', error);
                // Mostrar mensaje de error en el modal
                document.getElementById('finanzaId').textContent = 'Error al cargar los datos';
            });
    });
    
    // Limpiar los campos cuando se cierra el modal
    detailsModal.addEventListener('hidden.bs.modal', function () {
        const campos = [
            'finanzaId', 'finanzaAlumno', 'finanzaPagoInscripcion', 'finanzaDescuento',
            'finanzaValorSemestre', 'finanzaTotalPagadoSemestre', 'finanzaDeudaTotalSemestre',
            'finanzaEstado', 'finanzaFechaCreacion', 'finanzaFechaActualizacion',
            'finanzaPago1', 'finanzaPago2', 'finanzaPago3', 'finanzaPago4', 'finanzaPago5'
        ];
        
        campos.forEach(campo => {
            document.getElementById(campo).textContent = '';
        });
    });
});