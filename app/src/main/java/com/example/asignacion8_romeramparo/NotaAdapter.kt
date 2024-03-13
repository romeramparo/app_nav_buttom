import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.asignacion8_romeramparo.Nota
import com.example.asignacion8_romeramparo.R

class NotaAdapter(private var notas: List<Nota>) : RecyclerView.Adapter<NotaAdapter.NotaViewHolder>() {

    private val mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nota, parent, false)
        return NotaViewHolder(view, mediaPlayer)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val nota = notas[position]
        holder.bind(nota)
    }

    override fun getItemCount(): Int {
        return notas.size
    }

    fun actualizarLista(nuevasNotas: List<Nota>) {
        notas = nuevasNotas
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: NotaViewHolder) {
        super.onViewRecycled(holder)
        holder.stopAudio()
    }

    class NotaViewHolder(itemView: View, private val mediaPlayer: MediaPlayer) : RecyclerView.ViewHolder(itemView) {

        private val tituloTextView: TextView = itemView.findViewById(R.id.textViewTitulo)
        private val fechaTextView: TextView = itemView.findViewById(R.id.textViewFecha)
        private val vivenciaTextView: TextView = itemView.findViewById(R.id.textViewVivencia)
        private val imagenView: ImageView = itemView.findViewById(R.id.imagen_vw)

        fun bind(nota: Nota) {
            tituloTextView.text = nota.titulo
            fechaTextView.text = nota.fecha
            vivenciaTextView.text = nota.vivencia

            // Cargar imagen usando Glide
            Glide.with(itemView.context)
                .load(nota.imagenUrl)
                .into(imagenView)

            // Configurar y cargar audio
            mediaPlayer.reset()
            try {
                mediaPlayer.setDataSource(nota.audioFilePath)
                mediaPlayer.prepareAsync()
            } catch (e: Exception) {
                // Manejar errores si es necesario
            }
        }

        fun stopAudio() {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
        }
    }
}
