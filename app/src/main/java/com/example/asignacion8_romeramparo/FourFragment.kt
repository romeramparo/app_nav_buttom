package com.example.asignacion8_romeramparo

import NotaAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import android.widget.ImageView
import android.widget.Toast

class FourFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notaAdapter: NotaAdapter
    private val notas: MutableList<Nota> = mutableListOf()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var btnEliminarTodas: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notaAdapter = NotaAdapter(notas)
        databaseReference = FirebaseDatabase.getInstance().getReference("notas")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_four, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewVivencias)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = notaAdapter
        btnEliminarTodas = view.findViewById(R.id.btn_eliminar_todas)
        btnEliminarTodas.setOnClickListener {
            eliminarTodasLasNotas()
        }
        obtenerNotasDesdeFirebase()
        return view
    }

    private fun obtenerNotasDesdeFirebase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                notas.clear()

                for (snapshot in dataSnapshot.children) {
                    val nota = snapshot.getValue(Nota::class.java)
                    nota?.let {
                        cargarImagenParaNota(it)
                        notas.add(it)
                    }
                }

                notaAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores si es necesario
            }
        })
    }

    private fun cargarImagenParaNota(nota: Nota) {
        obtenerUrlImagenParaNota(nota.id) { imageUrl ->
            // Cargar la imagen con Glide
            imageUrl?.let {
                // Accede a la vista de cada elemento del RecyclerView y carga la imagen
                val notaViewHolder = recyclerView.findViewHolderForAdapterPosition(notas.indexOf(nota))
                if (notaViewHolder != null && notaViewHolder is NotaAdapter.NotaViewHolder) {
                    Glide.with(requireContext())
                        .load(it)
                        .into(notaViewHolder.itemView.findViewById(R.id.imagen_vw))
                }
            }
        }
    }

    private fun obtenerUrlImagenParaNota(notaId: String, listener: (String?) -> Unit) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("notas")

        // Buscar la nota en la base de datos por su ID
        databaseReference.child(notaId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val nota = dataSnapshot.getValue(Nota::class.java)

                // Verificar si la nota y la URL de la imagen no son nulas
                if (nota != null && !nota.imagenUrl.isNullOrEmpty()) {
                    // Llamar al listener con la URL de la imagen
                    listener(nota.imagenUrl)
                } else {
                    // Si la nota o la URL de la imagen es nula, llamar al listener con null
                    listener(null)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores si es necesario
                listener(null)
            }
        })
    }

    private fun eliminarTodasLasNotas() {
        databaseReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Todas las notas han sido eliminadas", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Error al eliminar las notas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
