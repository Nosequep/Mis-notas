package valera.jesus.misnotas

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.*

class MainActivity : AppCompatActivity() {
    var notas = ArrayList<Nota>()
    lateinit var adaptador: AdaptadorNotas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var fab = findViewById(R.id.fab) as FloatingActionButton

        fab.setOnClickListener{
            var intent = Intent(this, AgregarNotaActivity::class.java)
            startActivityForResult(intent, 123)
        }

        leerNotas()

        adaptador = AdaptadorNotas(this, notas)
        var listview: ListView = findViewById(R.id.listview)
        listview.adapter = adaptador
    }

    fun leerNotas(){
        notas.clear()
        var carpeta = File(ubicacion().absolutePath)

        if(carpeta.exists()){
            var archivos = carpeta.listFiles()
            if(archivos != null){
                for(archivo in archivos){
                    leerArchivo(archivo)
                }
            }
        }
    }

    fun leerArchivo(archivo: File){
        val fis = FileInputStream(archivo)
        val di = DataInputStream(fis)
        val br = BufferedReader(InputStreamReader(di))
        var strLine: String? = br.readLine()
        var myData = ""

        while (strLine != null){
            myData = myData + strLine
            strLine = br.readLine()
        }
        br.close()
        di.close()
        fis.close()

        var nombre = archivo.name.substring(0, archivo.name.length-4)

        var nota = Nota(nombre, myData)
        notas.add(nota)

    }

    private fun ubicacion(): File{
        val folder = File(getExternalFilesDir(null), "notas")
        if(!folder.exists()){
            folder.mkdir()
        }
        return folder
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 123){
            leerNotas()
            adaptador.notifyDataSetChanged()
        }
    }

    class AdaptadorNotas: BaseAdapter {
        var context: Context
        var notas = ArrayList<Nota>()

        constructor(context: Context, notas: ArrayList<Nota>){
            this.context = context
            this.notas = notas
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var inflador = LayoutInflater.from(context)
            var vista = inflador.inflate(R.layout.nota_layout, null)
            var nota = notas[position]

            var titulo = vista.findViewById(R.id.tv_titulo_det) as TextView
            var contenido = vista.findViewById(R.id.tv_contenido_det) as TextView

            titulo.text = nota.titulo
            contenido.text = nota.contenido

            var b_borrar: ImageView = vista.findViewById(R.id.btn_borrar)
            b_borrar.setOnClickListener{
                eliminar(nota.titulo)
                notas.remove(nota)
                this.notifyDataSetChanged()
            }

            return vista
        }

        override fun getItem(position: Int): Any {
            return notas[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return notas.size
        }

        private fun eliminar(titulo: String){
            if(titulo == ""){
                Toast.makeText(context, "Error: título vacío", Toast.LENGTH_SHORT).show()
            }else{
                try{
                    val archivo = File(ubicacion(), titulo +".txt")
                    archivo.delete()
                    Toast.makeText(context, "Se eliminó el archivo", Toast.LENGTH_SHORT).show()
                }catch (e: Exception){
                    Toast.makeText(context, "Error al eliminar el archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun ubicacion(): String{
            val album = File(context?.getExternalFilesDir(null), "notas")
            if(!album.exists()){
                album.mkdir()
            }
            return album.absolutePath
        }
    }
}