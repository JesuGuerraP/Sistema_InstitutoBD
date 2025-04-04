var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");

$(document).ajaxSend(function(e, xhr, options) {
    if(header && token) {
        xhr.setRequestHeader(header, token);
    }
});

// Obtener el botón de cierre de sesión
const logoutBtn = document.getElementById('logoutBtn');

// Agregar un evento click al botón
logoutBtn.addEventListener('click', (event) => {
    event.preventDefault(); // Evitar que el enlace se ejecute directamente

    // Abrir el modal manualmente
    const logoutModal = new bootstrap.Modal(document.getElementById('logoutModal'));
    logoutModal.show();
});

// Función Buscar profesores
function buscarProfesores() {
    const searchTerm = document.getElementById('searchInput').value;
    const resultContainer = document.querySelector('tbody'); // Selecciona el tbody de la tabla
    resultContainer.innerHTML = ''; // Limpiar resultados previos

    const endpoint = searchTerm.trim() === '' ? '/profesores' : `/profesores/buscarProfesores?searchTerm=${encodeURIComponent(searchTerm)}`;

    fetch(endpoint)
        .then(response => {
            if (!response.ok) {
                throw new Error('Error en la solicitud');
            }
            return response.json();
        })
        .then(profesores => {
            if (profesores.length === 0) {
                const noResultsRow = document.createElement('tr');
                noResultsRow.innerHTML = '<td colspan="6" class="text-center">No se encontraron resultados.</td>';
                resultContainer.appendChild(noResultsRow);
                return;
            }

            profesores.forEach(profesor => {
                const profesorRow = document.createElement('tr');
                profesorRow.innerHTML = `
                    <td>${profesor.id}</td>
                    <td>${profesor.nombres}</td>
                    <td>${profesor.apellidos}</td>
                    <td>${profesor.identificacion}</td>
                    <td>${profesor.correoElectronico}</td>
                    <td>
                        <button type="button" class="btn btn-warning btn-sm"
                                onclick="verDetalles(${profesor.id})"
                                data-bs-toggle="modal"
                                data-bs-target="#profesorModal">
                            👁️ Ver
                        </button>
                        <a href="/profesores/editar/${profesor.id}" class="btn btn-warning btn-sm">✏️ Editar</a>
                        <button type="button" class="btn btn-danger btn-sm" onclick="eliminarProfesor(${profesor.id})"> 🗑️ Eliminar </button>
                    </td>
                `;
                resultContainer.appendChild(profesorRow);
            });
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function verDetalles(id) {
    console.log("Solicitando detalles del profesor con ID:", id);

    $.ajax({
        url: '/profesores/detalles/' + id,
        type: 'GET',
        contentType: 'application/json',
        success: function(profesor) {
            console.log("Datos recibidos del profesor:", profesor);

            // Actualizar los campos del modal con los datos recibidos
            $('#profesor-id').text(profesor.id || '');
            $('#profesor-nombres').text(profesor.nombres || '');
            $('#profesor-apellidos').text(profesor.apellidos || '');
            $('#profesor-identificacion').text(profesor.identificacion || '');
            $('#profesor-genero').text(profesor.genero || '');
            $('#profesor-direccion').text(profesor.direccion || '');
            $('#profesor-telefono').text(profesor.telefono || '');
            $('#profesor-correoElectronico').text(profesor.correoElectronico || '');
            $('#profesor-fechaIngreso').text(profesor.fechaIngreso || '');
            $('#profesor-nivelAcademico').text(profesor.nivelAcademico || '');

            // Mostrar el modal
            $('#profesorModal').modal('show');
        },
        error: function(xhr, status, error) {
            console.error("Error al obtener detalles del profesor:");
            console.error("Estado:", status);
            console.error("Error:", error);
            console.error("Respuesta:", xhr.responseText);
            alert('No se pudieron cargar los detalles del profesor. Por favor, intente nuevamente.');
        }
    });
}

function confirmLogout() {
    return confirm("¿Estás seguro de que deseas cerrar sesión?");
}

// Limpiar el modal cuando se cierre
$('#profesorModal').on('hidden.bs.modal', function () {
    $('#profesor-id').text('');
    $('#profesor-nombres').text('');
    $('#profesor-apellidos').text('');
    $('#profesor-identificacion').text('');
    $('#profesor-genero').text('');
    $('#profesor-direccion').text('');
    $('#profesor-telefono').text('');
    $('#profesor-correoElectronico').text('');
    $('#profesor-fechaIngreso').text('');
    $('#profesor-nivelAcademico').text('');
});

function eliminarProfesor(id) {
    console.log("Intentando eliminar profesor con ID:", id);
    Swal.fire({
        title: '¿Estás seguro?',
        text: "Esta acción no se puede deshacer",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: '/profesores/eliminar/' + id,
                type: 'POST',
                success: function(response) {
                    console.log("Éxito - Respuesta del servidor:", response);
                    Swal.fire(
                        '¡Eliminado!',
                        'El profesor ha sido eliminado.',
                        'success'
                    ).then(() => {
                        window.location.reload();
                    });
                },
                error: function(xhr, status, error) {
                    console.log("Error:", error);
                    Swal.fire(
                        'Error',
                        'No se pudo eliminar el profesor.',
                        'error'
                    );
                }
            });
        }
    });
}
