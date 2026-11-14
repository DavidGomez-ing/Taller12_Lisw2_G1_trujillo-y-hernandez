package co.edu.unicauca.saberpro.infrastructure.adapter.out.notification;

import co.edu.unicauca.saberpro.domain.model.Pregunta;
import co.edu.unicauca.saberpro.domain.ports.outbound.NotificacionPort;

/** ADAPTADOR DE SALIDA (notificaciones): escribe el aviso en el log de la aplicacion. */
public class LogNotificacionAdapter implements NotificacionPort {

    private static final System.Logger LOG = System.getLogger(LogNotificacionAdapter.class.getName());

    @Override
    public void notificarEnvioARevision(Pregunta pregunta) {
        LOG.log(System.Logger.Level.INFO,
                "Pregunta {0} enviada a revision (estado {1}).", pregunta.getId(), pregunta.getEstado());
    }
}
