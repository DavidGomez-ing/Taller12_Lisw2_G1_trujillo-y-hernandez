package co.edu.unicauca.saberpro.domain.ports.outbound;

import co.edu.unicauca.saberpro.domain.model.Pregunta;

/**
 * PUERTO DE SALIDA (driven) de notificaciones. El dominio solo dice QUE hay que
 * avisar; el adaptador decide COMO (log, correo, cola de mensajes...).
 */
public interface NotificacionPort {

    void notificarEnvioARevision(Pregunta pregunta);
}
