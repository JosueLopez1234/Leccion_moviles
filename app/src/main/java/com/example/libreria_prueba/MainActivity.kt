package com.example.libreria_prueba

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.libreria_prueba.ui.theme.Libreria_pruebaTheme
import androidx.annotation.DrawableRes
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

// 1) MODELO DE DATOS (dummy, en memoria - NO Room, NO ViewModel)

data class Libro(
    val id: Int,
    val titulo: String,
    val autor: String,
    val año: Int,
    val precio: Double,
    @DrawableRes val imagenRes: Int,
    val descripcion: String
)

// Datos dummy: entre 6 y 8 registros, con valor numérico, imagen por default y texto.
val librosDummy = listOf(
    Libro(1, "Cien Años de Soledad", "Gabriel García Márquez", 1967, 15.99,
        R.drawable.cien_soledad,
        "Una obra maestra del realismo mágico..."),
    Libro(2, "1984", "George Orwell", 1949, 12.50,
        R.drawable._1984,
        "Una distopía sobre la vigilancia total y el totalitarismo."),
    Libro(3, "El Principito", "Antoine de Saint-Exupéry", 1943, 9.99,
        R.drawable.principito,
        "Un cuento poético sobre la amistad y la esencia de la vida."),
    Libro(4, "Fahrenheit 451", "Ray Bradbury", 1953, 11.25,
        R.drawable.fahrenheit,
        "En un futuro donde los libros están prohibidos, un bombero cuestiona su labor."),
    Libro(5, "Don Quijote de la Mancha", "Miguel de Cervantes", 1605, 18.00,
        R.drawable.don_quijote,
        "Las aventuras del ingenioso hidalgo y su fiel escudero Sancho Panza."),
    Libro(6, "Crimen y Castigo", "Fiódor Dostoyevski", 1866, 14.75,
        R.drawable.crimen,
        "Un estudiante comete un asesinato y enfrenta el peso moral de su acto."),
    Libro(7, "La Odisea", "Homero", -800, 13.40,
        R.drawable.odisea,
        "El largo viaje de regreso de Odiseo tras la guerra de Troya."),
    Libro(8, "Orgullo y Prejuicio", "Jane Austen", 1813, 10.90,
        R.drawable.orgullo,
        "Las tensiones sociales y románticas en la Inglaterra del siglo XIX.")
)

// 2) ENUM DE PANTALLAS

enum class Pantalla {
    HOME, CATALOGO, DETALLE
}

// 3) MAIN ACTIVITY o actividad principal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Libreria_pruebaTheme {
                LibreriaApp()
            }
        }
    }
}

@Composable
fun LibreriaApp() {
    // --- ESTADO ELEVADO --- y ademas esta el remember y mutablestateof
    // pantallaActual: decide qué pantalla se dibuja
    var pantallaActual by remember { mutableStateOf(Pantalla.HOME) }
    // libroSeleccionado: el ítem que el usuario tocó en el grid
    var libroSeleccionado by remember { mutableStateOf<Libro?>(null) }

    when (pantallaActual) {
        Pantalla.HOME -> HomeScreen(
            onVerCatalogo = { pantallaActual = Pantalla.CATALOGO }
        )
        Pantalla.CATALOGO -> CatalogoScreen(
            libros = librosDummy,
            onLibroClick = { libro ->
                libroSeleccionado = libro
                pantallaActual = Pantalla.DETALLE
            }
        )
        Pantalla.DETALLE -> {
            // Si por algún motivo no hay libro seleccionado, volvemos al catálogo
            val libro = libroSeleccionado
            if (libro != null) {
                DetalleScreen(
                    libro = libro,
                    onVolver = { pantallaActual = Pantalla.CATALOGO }
                )
            } else {
                pantallaActual = Pantalla.CATALOGO
            }
        }
    }
}

// 4) PANTALLA HOME o pantalla de home

@Composable
fun HomeScreen(onVerCatalogo: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Mi Librería",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bienvenido a tu catálogo digital de libros favoritos.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onVerCatalogo) {
                Text(stringResource(R.string.ver_catalogo))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Libreria_pruebaTheme {
        HomeScreen(onVerCatalogo = {})
    }
}

// 5) PANTALLA CATÁLOGO (Grid)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    libros: List<Libro>,
    onLibroClick: (Libro) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Catálogo de Libros") })
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(8.dp)
        ) {
            items(libros) { libro ->
                LibroItem(libro = libro, onClick = { onLibroClick(libro) })
            }
        }
    }
}

@Composable
fun LibroItem(libro: Libro, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = painterResource(id = libro.imagenRes),
                contentDescription = libro.titulo,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = libro.titulo, fontWeight = FontWeight.Bold, maxLines = 2)
            Text(text = "$${libro.precio}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LibroItemPreview() {
    Libreria_pruebaTheme {
        LibroItem(libro = librosDummy[0], onClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogoScreenPreview() {
    Libreria_pruebaTheme {
        CatalogoScreen(libros = librosDummy, onLibroClick = {})
    }
}

// 6) PANTALLA DETALLE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(libro: Libro, onVolver: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(libro.titulo) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al catálogo")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            AsyncImage(
                model = libro.imagenRes,
                contentDescription = libro.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = libro.titulo, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Autor: ${libro.autor}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Año: ${libro.año}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Precio: $${libro.precio}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = libro.descripcion, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleScreenPreview() {
    Libreria_pruebaTheme {
        DetalleScreen(libro = librosDummy[0], onVolver = {})
    }
}