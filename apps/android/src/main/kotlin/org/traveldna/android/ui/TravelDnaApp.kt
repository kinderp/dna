package org.traveldna.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.traveldna.android.pilot.PilotCatalog
import org.traveldna.android.pilot.PilotMilestone

enum class PilotScreen(val label: String) {
    Home("Home"),
    Pilots("Pilot"),
    Demo("Demo"),
    Study("Studio"),
}

@Composable
fun TravelDnaApp() {
    var selectedName by rememberSaveable { mutableStateOf(PilotScreen.Home.name) }
    val selected = PilotScreen.entries.firstOrNull { it.name == selectedName }
        ?: PilotScreen.Home

    Scaffold(
        bottomBar = {
            NavigationBar {
                PilotScreen.entries.forEach { screen ->
                    NavigationBarItem(
                        selected = selected == screen,
                        onClick = { selectedName = screen.name },
                        icon = {
                            Text(
                                text = screen.label.take(1),
                                modifier = Modifier.clearAndSetSemantics { },
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        label = { Text(screen.label) },
                    )
                }
            }
        },
    ) { padding ->
        when (selected) {
            PilotScreen.Home -> HomeScreen(padding)
            PilotScreen.Pilots -> PilotRoadmapScreen(padding)
            PilotScreen.Demo -> DemoScreen(padding)
            PilotScreen.Study -> StudyScreen(padding)
        }
    }
}

@Composable
private fun HomeScreen(padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                "Travel DNA",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                "Pilot 0 · shell Android didattica",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        item {
            InfoCard(
                title = "Che cosa dimostra",
                body = "La prima APK installabile usa i contratti condivisi e dati " +
                    "sintetici. Non richiede posizione, rete o account.",
            )
        }
        item {
            InfoCard(
                title = "Architettura",
                body = "apps/android è il composition root. I moduli shared restano " +
                    "provider-neutral e non importano API Android.",
            )
        }
        item {
            InfoCard(
                title = "Prossimo rischio reale",
                body = "Dopo la shell misureremo lifecycle, permessi foreground, " +
                    "batteria e handoff a un navigatore esterno.",
            )
        }
    }
}

@Composable
private fun PilotRoadmapScreen(padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { SectionTitle("Roadmap dei pilot") }
        items(PilotCatalog.milestones) { milestone -> PilotCard(milestone) }
    }
}

@Composable
private fun PilotCard(milestone: PilotMilestone) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                milestone.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(milestone.window, style = MaterialTheme.typography.labelLarge)
            Text(milestone.audience)
            Text("Risultati", fontWeight = FontWeight.Bold)
            milestone.outcomes.forEach { Text("• $it") }
            Text("Non-obiettivi", fontWeight = FontWeight.Bold)
            milestone.nonGoals.forEach { Text("• $it") }
        }
    }
}

@Composable
private fun DemoScreen(padding: PaddingValues) {
    val snapshot = remember { PilotCatalog.demoSnapshot() }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { SectionTitle("Snapshot deterministico") }
        item {
            InfoCard(
                title = "LocationSample",
                body = "sequence=${snapshot.sampleSequence}, " +
                    "monotonic=${snapshot.monotonicMillis} ms, " +
                    "lat=${snapshot.latitude}, lon=${snapshot.longitude}",
            )
        }
        item {
            InfoCard(
                title = "RouteCoordinate",
                body = "geometry index=${snapshot.geometryIndex}, " +
                    "fraction=${snapshot.fractionToNext}",
            )
        }
        item {
            InfoCard(
                title = "OffRoutePolicy",
                body = "conferma dopo ${snapshot.requiredSuspiciousCount} evidenze e " +
                    "almeno ${snapshot.minimumSuspiciousMillis} ms nel Lab sintetico.",
            )
        }
        item {
            Text(
                "Questi valori arrivano dai contratti TDNA reali, ma non " +
                    "rappresentano GPS o soglie sicure per la strada.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun StudyScreen(padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { SectionTitle("Percorso di studio") }
        items(PilotCatalog.studySteps) { step ->
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("${step.order}. ${step.title}", fontWeight = FontWeight.Bold)
                    Text(step.purpose)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Esercizio: ${step.repositoryExercise}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
        item { SectionTitle("Acquisti e prerequisiti") }
        items(PilotCatalog.optionalPurchases) { Text("• $it") }
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(6.dp))
            Text(body)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
