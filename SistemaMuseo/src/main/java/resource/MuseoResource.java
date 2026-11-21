package resource;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MuseoResource {

    @Inject
    EntityManager em;

    // 1. MUSEOS
    @GET
    @Path("/museos")
    public List<Museo> listarMuseos() {
        return em.createQuery("SELECT m FROM Museo m", Museo.class).getResultList();
    }

    @POST
    @Path("/museos")
    @Transactional
    public Response crearMuseo(Museo museo) {
        em.persist(museo);
        return Response.ok(museo).build();
    }

    // 2. SALAS DE EXPOSICIÓN
    @GET
    @Path("/salas")
    public List<SalaExposicion> listarSalas() {
        return em.createQuery("SELECT s FROM SalaExposicion s", SalaExposicion.class).getResultList();
    }

    @POST
    @Path("/salas")
    @Transactional
    public Response crearSala(SalaExposicion sala) {
        if (sala.getMuseo() != null && sala.getMuseo().getId() != null) {
            sala.setMuseo(em.find(Museo.class, sala.getMuseo().getId()));
        }
        em.persist(sala);
        return Response.ok(sala).build();
    }

    // 3. ARTISTAS
    @GET
    @Path("/artistas")
    public List<Artista> listarArtistas() {
        return em.createQuery("SELECT a FROM Artista a", Artista.class).getResultList();
    }

    @POST
    @Path("/artistas")
    @Transactional
    public Response crearArtista(Artista artista) {
        em.persist(artista);
        return Response.ok(artista).build();
    }

    // 4. CATEGORÍAS
    @GET
    @Path("/categorias")
    public List<Categoria> listarCategorias() {
        return em.createQuery("SELECT c FROM Categoria c", Categoria.class).getResultList();
    }

    @POST
    @Path("/categorias")
    @Transactional
    public Response crearCategoria(Categoria categoria) {
        em.persist(categoria);
        return Response.ok(categoria).build();
    }

    // 5. OBRAS (POLIMORFISMO)
    @GET
    @Path("/obras")
    @SuppressWarnings("rawtypes")
    public List listarObras() {
        List<Obra> obras = em.createQuery("SELECT o FROM Obra o", Obra.class).getResultList();
        List resultado = new ArrayList<>();
        for (Obra o : obras) {
            Map<String, Object> dto = new LinkedHashMap<>();
            Integer conservacion = o.getPorcentajeConservacion() != null ? o.getPorcentajeConservacion() : 0;
            dto.put("id", o.getId());
            dto.put("nombre", o.getNombre());
            dto.put("descripcion", o.getDescripcion());
            dto.put("porcentajeConservacion", conservacion);
            dto.put("tipo", o instanceof Pintura ? "Pintura" : "Escultura");
            dto.put("diagnostico", conservacion + "% (" + (conservacion >= 80 ? "Óptimo" : "Requiere revisión") + ")");
            dto.put("artista", o.getArtista() != null ? o.getArtista().getNombre() : "Sin artista");
            dto.put("categoria", o.getCategoria() != null ? o.getCategoria().getNombre() : "Sin categoría");
            dto.put("sala", o.getSalaActual() != null ? o.getSalaActual().getNombre() : "Bodega");
            resultado.add(dto);
        }
        return resultado;
    }

    @POST
    @Path("/obras")
    @Transactional
    public Response crearObra(Obra obra) {
        if (obra.getArtista() != null && obra.getArtista().getId() != null) {
            obra.setArtista(em.find(Artista.class, obra.getArtista().getId()));
        }
        if (obra.getCategoria() != null && obra.getCategoria().getId() != null) {
            obra.setCategoria(em.find(Categoria.class, obra.getCategoria().getId()));
        }
        if (obra.getSalaActual() != null && obra.getSalaActual().getId() != null) {
            obra.setSalaActual(em.find(SalaExposicion.class, obra.getSalaActual().getId()));
        }
        if (obra.getFechaCreacion() == null) {
            obra.setFechaCreacion(LocalDateTime.now());
        }
        if (obra.getFechaAdquisicion() == null) {
            obra.setFechaAdquisicion(LocalDateTime.now());
        }
        em.persist(obra);

        if (obra.getSalaActual() != null) {
            HistorialExhibicion historial = new HistorialExhibicion();
            historial.setObra(obra);
            historial.setSala(obra.getSalaActual());
            historial.setFechaInicio(LocalDate.now());
            em.persist(historial);
        }
        return Response.ok(obra).build();
    }

    // 6. RECORRIDOS GUIADOS
    @GET
    @Path("/recorridos")
    public List<RecorridoSala> listarRecorridos() {
        return em.createQuery("SELECT rs FROM RecorridoSala rs ORDER BY rs.recorrido.id, rs.ordenSecuencia", RecorridoSala.class).getResultList();
    }

    @POST
    @Path("/recorridos")
    @Transactional
    @SuppressWarnings("rawtypes")
    public Response crearRecorrido(Map payload) {
        RecorridoGuiado recorrido = new RecorridoGuiado();
        recorrido.setNombre((String) payload.get("nombre"));
        recorrido.setDescripcion((String) payload.get("descripcion"));
        recorrido.setUrlPagina((String) payload.get("urlPagina"));
        em.persist(recorrido);

        Object salasObj = payload.get("salasIds");
        if (salasObj instanceof List) {
            List salasIds = (List) salasObj;
            int orden = 1;
            for (Object idObj : salasIds) {
                Long salaId = Long.valueOf(idObj.toString());
                SalaExposicion sala = em.find(SalaExposicion.class, salaId);
                if (sala != null) {
                    RecorridoSala rs = new RecorridoSala();
                    rs.setRecorrido(recorrido);
                    rs.setSala(sala);
                    rs.setOrdenSecuencia(orden++);
                    em.persist(rs);
                }
            }
        }
        return Response.ok(recorrido).build();
    }

    // 7. HISTORIAL DE EXHIBICIÓN
    @GET
    @Path("/historial")
    public List<HistorialExhibicion> listarHistorial() {
        return em.createQuery("SELECT h FROM HistorialExhibicion h", HistorialExhibicion.class).getResultList();
    }

    // 8. ENDPOINTS DE ELIMINACIÓN (DELETE)
    @DELETE
    @Path("/obras/{id}")
    @Transactional
    public Response eliminarObra(@PathParam("id") Long id) {
        Obra obra = em.find(Obra.class, id);
        if (obra == null) return Response.status(Response.Status.NOT_FOUND).build();
        em.createQuery("DELETE FROM HistorialExhibicion h WHERE h.obra.id = :id").setParameter("id", id).executeUpdate();
        em.remove(obra);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/museos/{id}")
    @Transactional
    public Response eliminarMuseo(@PathParam("id") Long id) {
        Museo museo = em.find(Museo.class, id);
        if (museo == null) return Response.status(Response.Status.NOT_FOUND).build();
        Long salasAsociadas = em.createQuery("SELECT COUNT(s) FROM SalaExposicion s WHERE s.museo.id = :id", Long.class)
                .setParameter("id", id).getSingleResult();
        if (salasAsociadas > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("No se puede eliminar: este museo tiene salas registradas.").build();
        }
        em.remove(museo);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/salas/{id}")
    @Transactional
    public Response eliminarSala(@PathParam("id") Long id) {
        SalaExposicion sala = em.find(SalaExposicion.class, id);
        if (sala == null) return Response.status(Response.Status.NOT_FOUND).build();
        Long obrasEnSala = em.createQuery("SELECT COUNT(o) FROM Obra o WHERE o.salaActual.id = :id", Long.class)
                .setParameter("id", id).getSingleResult();
        if (obrasEnSala > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("No se puede eliminar: hay obras asignadas a esta sala.").build();
        }
        em.createQuery("DELETE FROM RecorridoSala rs WHERE rs.sala.id = :id").setParameter("id", id).executeUpdate();
        em.createQuery("DELETE FROM HistorialExhibicion h WHERE h.sala.id = :id").setParameter("id", id).executeUpdate();
        em.remove(sala);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/artistas/{id}")
    @Transactional
    public Response eliminarArtista(@PathParam("id") Long id) {
        Artista artista = em.find(Artista.class, id);
        if (artista == null) return Response.status(Response.Status.NOT_FOUND).build();
        Long obrasArtista = em.createQuery("SELECT COUNT(o) FROM Obra o WHERE o.artista.id = :id", Long.class)
                .setParameter("id", id).getSingleResult();
        if (obrasArtista > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("No se puede eliminar: el artista tiene obras registradas.").build();
        }
        em.remove(artista);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/categorias/{id}")
    @Transactional
    public Response eliminarCategoria(@PathParam("id") Long id) {
        Categoria cat = em.find(Categoria.class, id);
        if (cat == null) return Response.status(Response.Status.NOT_FOUND).build();
        Long obrasCat = em.createQuery("SELECT COUNT(o) FROM Obra o WHERE o.categoria.id = :id", Long.class)
                .setParameter("id", id).getSingleResult();
        if (obrasCat > 0) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("No se puede eliminar: existen obras en esta categoría.").build();
        }
        em.remove(cat);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/recorridos/{id}")
    @Transactional
    public Response eliminarRecorridoSala(@PathParam("id") Long id) {
        RecorridoSala rs = em.find(RecorridoSala.class, id);
        if (rs == null) return Response.status(Response.Status.NOT_FOUND).build();
        em.remove(rs);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/historial/{id}")
    @Transactional
    public Response eliminarHistorial(@PathParam("id") Long id) {
        HistorialExhibicion h = em.find(HistorialExhibicion.class, id);
        if (h == null) return Response.status(Response.Status.NOT_FOUND).build();
        em.remove(h);
        return Response.noContent().build();
    }
}