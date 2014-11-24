/**
 * @file AgentConfigurationPanel.java
 * @date 22/11/2014
 */
package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Se trata de un panel de configuración de agentes, que permite al usuario
 * configurar un agente dependiendo del tipo que sea.
 *
 * Los controles para aceptar o cancelar la configuración deben ser implementados
 * desde el exterior y utilizarse para llamar a los métodos "accept()" o
 * "cancel", respectivamente.
 */
public abstract class AgentConfigurationPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private JPanel m_root;
  private ArrayList<EventListener> m_listeners;

  protected ArrayList<String> m_errors;
  protected ArrayList<String> m_success;

  /**
   * Interfaz de escucha de eventos de tipo "Exitoso". Estos eventos son
   * notificados cuando se llama a {@code onSuccess()}.
   */
  public static interface EventListener {
    /**
     * LLamado cuando ocurre el evento de tipo "Exitoso". Estos eventos son
     * notificados cuando se llama a {@code onSuccess()}.
     * @param msgs Lista de mensajes que se quieren mostrar al usuario.
     */
    public void onSuccess (ArrayList<String> msgs);

    /**
     * LLamado cuando ocurre el evento de tipo "Cancelar". Estos eventos son
     * notificados cuando se llama a {@code onCancel()}.
     */
    public void onCancel ();

    /**
     * LLamado cuando ocurre el evento de tipo "Error". Estos eventos son
     * notificados cuando se llama a {@code onError()}.
     * @param errors Lista de mensajes de error a mostrar al usuario.
     */
    public void onError (ArrayList<String> errors);
  }

  /**
   * Construye la interfaz del panel de configuración de agentes.
   */
  public AgentConfigurationPanel () {
    m_root = new JPanel();
    m_listeners = new ArrayList<EventListener>();
    m_errors = new ArrayList<String>();
    m_success = new ArrayList<String>();

    createGUI(m_root);
    createControls();
  }

  /**
   * Añade un oyente de eventos.
   * @param listener Clase oyente que se quiere añadir.
   */
  public final void addEventListener (EventListener listener) {
    if (!m_listeners.contains(listener))
      m_listeners.add(listener);
  }

  /**
   * Elimina un oyente de eventos. Si no es un oyente, la lista de oyentes
   * permanece intacta.
   * @param listener Clase oyente que se quiere añadir.
   */
  public final void removeEventListener (EventListener listener) {
    m_listeners.remove(listener);
  }

  /**
   * Provoca que la configuración actualmente almacenada en el panel de
   * configuración se guarde en el agente, modificando su comportamiento.
   *
   * Este método debe ser implementado por cada agente.
   * @return <ul>
   *           <li><b>true</b> si se pudo guardar el resultado.</li>
   *           <li><b>false</b> si la configuración indicada no es válida.</li>
   *         </ul>
   */
  protected abstract boolean accept ();

  /**
   * Cancela la operación de configuración, dejando al agente en su estado
   * de partida.
   *
   * Este método debe ser implementado por cada agente.
   */
  protected abstract void cancel ();

  /**
   * Crea la interfaz gráfica de usuario, que es la que se mostrará al mismo.
   * Estará personalizada para el agente específico, pero no incluirá los botones
   * de "Aceptar" y "Cancelar".
   * @param root Panel padre de todos los elementos que se creen. Si se intenta
   *        utilizar el panel padre de la clase en lugar de éste, el panel de
   *        configuración no se mostrará correctamente.
   */
  protected abstract void createGUI (JPanel root);

  /**
   * Se crean los controles de "Aceptar" y "Cancelar" y los coloca en el panel
   * junto a los controles personalizados del agente, que ya deben haber sido
   * creados.
   */
  private void createControls () {
    setLayout(new BorderLayout());

    JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton accept = new JButton("OK");
    JButton cancel = new JButton("Cancel");

    accept.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
        if (accept())
          onSuccess();
        else
          onError();
      }
    });

    cancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed (ActionEvent e) {
        cancel();
        onCancel();
      }
    });

    controls.add(accept);
    controls.add(cancel);

    add(m_root, BorderLayout.CENTER);
    add(controls, BorderLayout.SOUTH);
  }

  /**
   * Método llamado cuando el usuario aplica los cambios y éstos son guardados
   * correctamente. Notifica a todos los {@code SuccessListener}.
   */
  private void onSuccess () {
    for (EventListener listener: m_listeners)
      listener.onSuccess(m_success);
  }

  /**
   * Método llamado cuando el usuario cancela la operación y el agente ha
   * quedado como al principio. Notifica a todos los {@code CancelListener}.
   */
  private void onCancel () {
    for (EventListener listener: m_listeners)
      listener.onCancel();
  }

  /**
   * Método llamado cuando el usuario intenta aplicar los cambios y no es
   * posible porque la configuración no es válida. Notifica a todos los
   * {@code ErrorListener}.
   */
  private void onError () {
    for (EventListener listener: m_listeners)
      listener.onError(m_errors);
  }
}