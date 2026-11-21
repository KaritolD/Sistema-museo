const API = '/api';

// Resaltar el botón activo en la barra superior al navegar
function marcarActivo(btn) {
    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
    if (btn) btn.classList.add('active');
}

// Alternar entre campos de Pintura y Escultura
function toggleCamposObra() {
    const tipo = document.getElementById('obra-tipo').value;
    document.getElementById('campos-pintura').style.display = tipo === 'PINTURA' ? 'block' : 'none';
    document.getElementById('campos-escultura').style.display = tipo === 'ESCULTURA' ? 'block' : 'none';
}

// Habilitar el formulario de la obra cuando Artista, Categoría y Sala estén seleccionados
function verificarPasoInicial() {
    const artista = document.getElementById('obra-artista').value;
    const categoria = document.getElementById('obra-categoria').value;
    const sala = document.getElementById('obra-sala').value;
    const bloqueDatos = document.getElementById('paso-datos-obra');

    if (artista && categoria && sala) {
        bloqueDatos.classList.add('unlocked');
    } else {
        bloqueDatos.classList.remove('unlocked');
    }
}

// Mostrar notificación flotante
function showToast(msg) {
    const toast = document.getElementById('toast');
    toast.textContent = msg;
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3600);
}

// Eliminar cualquier registro con confirmación previa
async function eliminarRegistro(endpoint, id, nombreEntidad) {
    const confirmado = confirm(`¿Estás segura de que deseas eliminar ${nombreEntidad}? Esta acción no se puede deshacer.`);
    if (!confirmado) return;

    try {
        const res = await fetch(`${API}/${endpoint}/${id}`, {
            method: 'DELETE'
        });

        if (res.ok) {
            showToast('Registro eliminado correctamente');
            cargarTodo();
        } else if (res.status === 409) {
            const mensaje = await res.text();
            showToast(mensaje || 'No se puede eliminar porque tiene registros asociados.');
        } else {
            showToast('No se pudo eliminar el registro');
        }
    } catch (e) {
        console.error(e);
        showToast('Error de conexión al intentar eliminar');
    }
}

// Cargar todas las tablas y selectores al iniciar
async function cargarTodo() {
    await Promise.all([
        cargarMuseos(),
        cargarSalas(),
        cargarArtistas(),
        cargarCategorias(),
        cargarObras(),
        cargarRecorridos(),
        cargarHistorial()
    ]);
}

// 1. MUSEOS
async function cargarMuseos() {
    try {
        const res = await fetch(`${API}/museos`);
        const data = await res.json();
        const tbody = document.getElementById('tabla-museos');
        const select = document.getElementById('sala-museo');

        tbody.innerHTML = data.map(m => `
            <tr>
                <td>${m.id}</td>
                <td><strong>${m.nombre}</strong></td>
                <td>${m.latitud ?? '-'}, ${m.longitud ?? '-'}</td>
                <td>${m.urlPagina ? `<a href="${m.urlPagina}" target="_blank">Visitar</a>` : '-'}</td>
                <td>
                    <button class="btn-delete" onclick="eliminarRegistro('museos', ${m.id}, 'el museo &quot;${m.nombre}&quot;')">Eliminar</button>
                </td>
            </tr>
        `).join('');

        select.innerHTML = '<option value="">-- Selecciona museo --</option>' +
            data.map(m => `<option value="${m.id}">${m.nombre}</option>`).join('');
    } catch (e) {
        console.error(e);
    }
}

async function guardarMuseo(e) {
    e.preventDefault();
    const payload = {
        nombre: document.getElementById('museo-nombre').value,
        descripcion: document.getElementById('museo-desc').value,
        urlPagina: document.getElementById('museo-url').value || null,
        latitud: parseFloat(document.getElementById('museo-lat').value) || null,
        longitud: parseFloat(document.getElementById('museo-lng').value) || null
    };
    const res = await fetch(`${API}/museos`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (res.ok) {
        e.target.reset();
        showToast('Sede museística registrada');
        cargarMuseos();
    }
}

// 2. SALAS
async function cargarSalas() {
    try {
        const res = await fetch(`${API}/salas`);
        const data = await res.json();
        const tbody = document.getElementById('tabla-salas');
        const selectObra = document.getElementById('obra-sala');
        const checklist = document.getElementById('checklist-salas');

        tbody.innerHTML = data.map(s => `
            <tr>
                <td>${s.id}</td>
                <td><strong>${s.nombre}</strong></td>
                <td>${s.capacidad} personas</td>
                <td>${s.museo ? s.museo.nombre : '-'}</td>
                <td>
                    <button class="btn-delete" onclick="eliminarRegistro('salas', ${s.id}, 'la sala &quot;${s.nombre}&quot;')">Eliminar</button>
                </td>
            </tr>
        `).join('');

        selectObra.innerHTML = '<option value="">-- Sala --</option>' +
            data.map(s => `<option value="${s.id}">${s.nombre}</option>`).join('');

        checklist.innerHTML = data.length
            ? data.map(s => `
                <label>
                    <input type="checkbox" name="salas-rec" value="${s.id}">
                    ${s.nombre} (${s.museo ? s.museo.nombre : 'Sin sede'})
                </label>
            `).join('')
            : '<small>Primero crea salas en la sección Salas</small>';
    } catch (e) {
        console.error(e);
    }
}

async function guardarSala(e) {
    e.preventDefault();
    const payload = {
        nombre: document.getElementById('sala-nombre').value,
        descripcion: document.getElementById('sala-desc').value,
        capacidad: parseInt(document.getElementById('sala-capacidad').value),
        museo: { id: parseInt(document.getElementById('sala-museo').value) }
    };
    const res = await fetch(`${API}/salas`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (res.ok) {
        e.target.reset();
        showToast('Sala de exposición creada');
        cargarSalas();
    }
}

// 3. ARTISTAS
async function cargarArtistas() {
    try {
        const res = await fetch(`${API}/artistas`);
        const data = await res.json();
        document.getElementById('tabla-artistas').innerHTML = data.map(a => `
            <tr>
                <td>${a.id}</td>
                <td><strong>${a.nombre}</strong></td>
                <td>${a.nacionalidad || '-'}</td>
                <td>
                    <button class="btn-delete" onclick="eliminarRegistro('artistas', ${a.id}, 'al artista &quot;${a.nombre}&quot;')">Eliminar</button>
                </td>
            </tr>
        `).join('');

        document.getElementById('obra-artista').innerHTML = '<option value="">-- Artista --</option>' +
            data.map(a => `<option value="${a.id}">${a.nombre}</option>`).join('');
    } catch (e) {
        console.error(e);
    }
}

async function guardarArtista(e) {
    e.preventDefault();
    const payload = {
        nombre: document.getElementById('artista-nombre').value,
        nacionalidad: document.getElementById('artista-nac').value,
        biografia: document.getElementById('artista-bio').value
    };
    const res = await fetch(`${API}/artistas`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (res.ok) {
        e.target.reset();
        showToast('Artista registrado');
        cargarArtistas();
    }
}

// 4. CATEGORÍAS
async function cargarCategorias() {
    try {
        const res = await fetch(`${API}/categorias`);
        const data = await res.json();
        document.getElementById('tabla-categorias').innerHTML = data.map(c => `
            <tr>
                <td>${c.id}</td>
                <td><strong>${c.nombre}</strong></td>
                <td>${c.descripcion || '-'}</td>
                <td>
                    <button class="btn-delete" onclick="eliminarRegistro('categorias', ${c.id}, 'la categoría &quot;${c.nombre}&quot;')">Eliminar</button>
                </td>
            </tr>
        `).join('');

        document.getElementById('obra-categoria').innerHTML = '<option value="">-- Categoría --</option>' +
            data.map(c => `<option value="${c.id}">${c.nombre}</option>`).join('');
    } catch (e) {
        console.error(e);
    }
}

async function guardarCategoria(e) {
    e.preventDefault();
    const payload = {
        nombre: document.getElementById('cat-nombre').value,
        descripcion: document.getElementById('cat-desc').value
    };
    const res = await fetch(`${API}/categorias`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (res.ok) {
        e.target.reset();
        showToast('Categoría creada');
        cargarCategorias();
    }
}

// 5. OBRAS
async function cargarObras() {
    try {
        const res = await fetch(`${API}/obras`);
        const data = await res.json();
        document.getElementById('tabla-obras').innerHTML = data.map(o => `
            <tr>
                <td><strong>${o.nombre}</strong></td>
                <td>${o.tipo}</td>
                <td>${o.artista}</td>
                <td>${o.sala}</td>
                <td>${o.diagnostico}</td>
                <td>
                    <button class="btn-delete" onclick="eliminarRegistro('obras', ${o.id}, 'la obra &quot;${o.nombre}&quot;')">Eliminar</button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        console.error(e);
    }
}

async function guardarObra(e) {
    e.preventDefault();
    const tipo = document.getElementById('obra-tipo').value;
    const fCreacion = document.getElementById('obra-fecha-creacion').value;
    const fAdquisicion = document.getElementById('obra-fecha-adquisicion').value;

    const payload = {
        tipoObra: tipo,
        nombre: document.getElementById('obra-nombre').value,
        descripcion: document.getElementById('obra-desc').value,
        porcentajeConservacion: parseInt(document.getElementById('obra-conservacion').value),
        imagenUrl: document.getElementById('obra-imagen').value || null,
        fechaCreacion: fCreacion ? `${fCreacion}T00:00:00` : null,
        fechaAdquisicion: fAdquisicion ? `${fAdquisicion}T00:00:00` : null,
        artista: { id: parseInt(document.getElementById('obra-artista').value) },
        categoria: { id: parseInt(document.getElementById('obra-categoria').value) },
        salaActual: { id: parseInt(document.getElementById('obra-sala').value) }
    };

    if (tipo === 'PINTURA') {
        payload.tecnica = document.getElementById('pintura-tecnica').value;
        payload.dimensiones = document.getElementById('pintura-dimensiones').value;
    } else {
        payload.material = document.getElementById('escultura-material').value;
        payload.pesoKg = parseFloat(document.getElementById('escultura-peso').value) || 0;
    }

    const res = await fetch(`${API}/obras`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (res.ok) {
        e.target.reset();
        toggleCamposObra();
        verificarPasoInicial();
        showToast('Obra catalogada y asignada a sala');
        cargarObras();
        cargarHistorial();
    } else {
        showToast('Error al guardar la obra. Verifica los campos.');
    }
}

// 6. RECORRIDOS
async function cargarRecorridos() {
    try {
        const res = await fetch(`${API}/recorridos`);
        const data = await res.json();
        document.getElementById('tabla-recorridos').innerHTML = data.map(r => `
            <tr>
                <td><strong>${r.recorrido ? r.recorrido.nombre : '-'}</strong></td>
                <td>#${r.ordenSecuencia}</td>
                <td>${r.sala ? r.sala.nombre : '-'}</td>
                <td>
                    <button class="btn-delete" onclick="eliminarRegistro('recorridos', ${r.id}, 'esta parada del recorrido')">Eliminar</button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        console.error(e);
    }
}

async function guardarRecorrido(e) {
    e.preventDefault();
    const salasIds = Array.from(document.querySelectorAll('input[name="salas-rec"]:checked'))
        .map(cb => parseInt(cb.value));

    const payload = {
        nombre: document.getElementById('rec-nombre').value,
        descripcion: document.getElementById('rec-desc').value,
        urlPagina: document.getElementById('rec-url').value,
        salasIds: salasIds
    };

    const res = await fetch(`${API}/recorridos`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });
    if (res.ok) {
        e.target.reset();
        showToast('Recorrido curatorial publicado');
        cargarRecorridos();
    }
}

// 7. HISTORIAL
async function cargarHistorial() {
    try {
        const res = await fetch(`${API}/historial`);
        const data = await res.json();
        document.getElementById('tabla-historial').innerHTML = data.map(h => `
            <tr>
                <td>${h.id}</td>
                <td><strong>${h.obra ? h.obra.nombre : '-'}</strong></td>
                <td>${h.sala ? h.sala.nombre : '-'}</td>
                <td>${h.fechaInicio || '-'}</td>
                <td>${h.fechaFin || 'En exhibición actual'}</td>
                <td>
                    <button class="btn-delete" onclick="eliminarRegistro('historial', ${h.id}, 'este registro del historial')">Eliminar</button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        console.error(e);
    }
}

document.addEventListener('DOMContentLoaded', cargarTodo);