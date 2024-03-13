package com.example.asignacion8_romeramparo

import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.Manifest


class SecondFragment : Fragment() {

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            iv_imagen.setImageURI(uri)
        } else {
            Log.i("aris", "No Seleccionada")
        }
    }

    lateinit var btn_imagen: Button
    lateinit var titulo_et: EditText
    lateinit var fecha_et: EditText
    lateinit var vivencia_et: EditText
    lateinit var btn_guardar: Button
    lateinit var btn_audio: Button
    lateinit var iv_imagen: ImageView
    private var mediaRecorder: MediaRecorder? = null
    private lateinit var audioFilePath: String
    private lateinit var databaseReference: DatabaseReference
    private var isRecording = false
    companion object {
        private const val REQUEST_AUDIO_PERMISSION_CODE = 123
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second, container, false)

        // Inicializar vistas y variables
        databaseReference = FirebaseDatabase.getInstance().reference.child("notas")
        btn_imagen = view.findViewById(R.id.btn_imagen)
        iv_imagen = view.findViewById(R.id.iv_imagen)
        btn_audio = view.findViewById(R.id.btn_audio)
        titulo_et = view.findViewById(R.id.titulo_et)
        fecha_et = view.findViewById(R.id.fecha_et)
        vivencia_et = view.findViewById(R.id.vivencia_et)
        btn_guardar = view.findViewById(R.id.btn_guardar)
        audioFilePath = requireContext().externalCacheDir?.absolutePath + "/audio.3gp"
        mediaRecorder = MediaRecorder()

        // Configurar listeners
        btn_imagen.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest())
        }
        btn_guardar.setOnClickListener {
            guardarNotaEnFirebase()
        }
        btn_audio.setOnClickListener {
            onAudioButtonClicked()
        }

        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_AUDIO_PERMISSION_CODE -> {
                // Verificar si el usuario concedió el permiso
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, puedes iniciar la grabación de audio
                    startRecording()
                } else {
                    // Permiso denegado, puedes mostrar un mensaje o tomar alguna acción
                    Toast.makeText(
                        requireContext(),
                        "Permiso de grabación de audio denegado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun onAudioButtonClicked() {
        if (isRecording) {
            stopRecording()
        } else {
            if (checkAudioPermission()) {
                startRecording()
            } else {
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_AUDIO_PERMISSION_CODE)
            }
        }
        // Actualizar el estado de grabación y el texto del botón
        isRecording = !isRecording
        btn_audio.text = if (isRecording) "Detener Grabación" else "Iniciar Grabación"
    }

    private fun startRecording() {
        try {
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("aris", "Error al iniciar la grabación: ${e.message}")
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
            }
        } catch (e: Exception) {
            Log.e("aris", "Error al detener la grabación: ${e.message}")
        }
    }

    private fun guardarNotaEnFirebase() {
        val titulo = titulo_et.text.toString()
        val fecha = fecha_et.text.toString()
        val vivencia = vivencia_et.text.toString()

        if (titulo.isEmpty() || fecha.isEmpty() || vivencia.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val nota = Nota(titulo, fecha, vivencia, audioFilePath)

        val nuevaNotaReference = databaseReference.push()
        nuevaNotaReference.setValue(nota).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Nota guardada con éxito", Toast.LENGTH_SHORT).show()
                titulo_et.text.clear()
                fecha_et.text.clear()
                vivencia_et.text.clear()

                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.bottom_navigation, FourFragment())
                    ?.addToBackStack(null)
                    ?.commit()
            } else {
                Toast.makeText(requireContext(), "Error al guardar la nota", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }
}
