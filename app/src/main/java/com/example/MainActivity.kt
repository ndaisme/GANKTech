package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.theme.GankColors
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GankViewModel
import com.example.ui.viewmodel.ServiceJob
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(
          modifier = Modifier.fillMaxSize(),
          contentWindowInsets = WindowInsets.safeDrawing,
          containerColor = GankColors.Paper
        ) { innerPadding ->
          GankTeknisiApp(
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

enum class GankTab {
    DASHBOARD,
    DETAIL_SERVIS,
    PENGATURAN
}

@Composable
fun GankTeknisiApp(
  modifier: Modifier = Modifier,
  viewModel: GankViewModel = viewModel()
) {
  val services by viewModel.services.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedFilter by viewModel.selectedStatusFilter.collectAsState()
  
  val isAddDialogOpen by viewModel.isAddJobDialogOpen.collectAsState()
  val isChecklistDialogOpen by viewModel.isChecklistDialogOpen.collectAsState()

  val activeJobChecklistId by viewModel.activeJobChecklistId.collectAsState()
  val activeJobChecklistIsBefore by viewModel.activeJobChecklistIsBefore.collectAsState()

  var currentTab by remember { mutableStateOf(GankTab.DASHBOARD) }

  // Derived statistics
  val totalJobs = services.size
  val pendingJobs = services.count { it.status in listOf("WAITING", "DIAGNOSA", "PENGERJAAN", "QC") }
  val completedJobs = services.count { it.status in listOf("SELESAI", "DIAMBIL") }
  val totalProfit = services.filter { it.status in listOf("SELESAI", "DIAMBIL") }.sumOf { it.cost }

  // Filter and search logic
  val filteredServices = services.filter { job ->
    val matchesSearch = job.customerName.contains(searchQuery, ignoreCase = true) ||
                        job.hpModel.contains(searchQuery, ignoreCase = true) ||
                        job.problem.contains(searchQuery, ignoreCase = true)
    
    val matchesFilter = if (selectedFilter == "ALL") true else job.status == selectedFilter
    matchesSearch && matchesFilter
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(GankColors.Paper)
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
    ) {
      // HEADER SECTION
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "GANK TEKNISI",
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            color = GankColors.Ink,
            fontFamily = FontFamily.Monospace,
            letterSpacing = (-0.5).sp
          )
          Text(
            text = "WEAPON OF CHOICE FOR PHONE REPAIRERS",
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = GankColors.Steel,
            letterSpacing = 1.sp
          )
        }
        NeoBrutalistBadge(
          text = "ONLINE & SYNCED",
          containerColor = GankColors.Green
        )
      }

      // ACTIVE TAB CONTENT
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        when (currentTab) {
          GankTab.DASHBOARD -> {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
            ) {
              // STATISTICS GRID
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                // Total Service Card
                Box(modifier = Modifier.weight(1f)) {
                  NeoBrutalistCard(
                    backgroundColor = GankColors.White,
                    shadowOffset = 4.dp,
                    borderWidth = 2.dp
                  ) {
                    Column(modifier = Modifier.padding(2.dp)) {
                      Text("ACTIVE JOBS", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = GankColors.Steel)
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(totalJobs.toString(), fontWeight = FontWeight.Black, fontSize = 24.sp, color = GankColors.Ink)
                    }
                  }
                }
                // Pending Card
                Box(modifier = Modifier.weight(1f)) {
                  NeoBrutalistCard(
                    backgroundColor = GankColors.Silver,
                    shadowOffset = 4.dp,
                    borderWidth = 2.dp
                  ) {
                    Column(modifier = Modifier.padding(2.dp)) {
                      Text("PENDING", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = GankColors.Steel)
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(pendingJobs.toString(), fontWeight = FontWeight.Black, fontSize = 24.sp, color = GankColors.Ink)
                    }
                  }
                }
                // Completed Card
                Box(modifier = Modifier.weight(1f)) {
                  NeoBrutalistCard(
                    backgroundColor = GankColors.GankYellow,
                    shadowOffset = 4.dp,
                    borderWidth = 2.dp
                  ) {
                    Column(modifier = Modifier.padding(2.dp)) {
                      Text("FINISHED", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = GankColors.Ink)
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(completedJobs.toString(), fontWeight = FontWeight.Black, fontSize = 24.sp, color = GankColors.Ink)
                    }
                  }
                }
              }

              // PROFIT BAR
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 16.dp)
              ) {
                NeoBrutalistCard(
                  backgroundColor = GankColors.White,
                  shadowOffset = 5.dp,
                  borderWidth = 3.dp
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column {
                      Text(
                        text = "TOTAL PROFIT HARI INI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = GankColors.Steel
                      )
                      Text(
                        text = formatRupiah(totalProfit),
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = GankColors.Green
                      )
                    }
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      tint = GankColors.Green,
                      modifier = Modifier
                        .size(36.dp)
                        .border(2.dp, GankColors.Ink, RoundedCornerShape(4.dp))
                        .background(GankColors.Paper)
                        .padding(4.dp)
                    )
                  }
                }
              }

              // QUICK ACTIONS BAR
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                NeoBrutalistButton(
                  text = "TAMBAH SERVIS",
                  icon = Icons.Default.Add,
                  onClick = { viewModel.openAddJobDialog() },
                  modifier = Modifier.weight(1f)
                )
                NeoBrutalistButton(
                  text = "STANDALONE TOOLKIT",
                  icon = Icons.Default.Build,
                  containerColor = GankColors.White,
                  onClick = { viewModel.openChecklistDialog() },
                  modifier = Modifier.weight(1f)
                )
              }

              // WORKSHOP BULLETIN & QUICK GUIDE
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 16.dp)
              ) {
                NeoBrutalistCard(
                  backgroundColor = GankColors.White,
                  shadowOffset = 5.dp,
                  borderWidth = 3.dp
                ) {
                  Column(modifier = Modifier.padding(6.dp)) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = GankColors.GankYellow,
                        modifier = Modifier
                          .size(24.dp)
                          .background(GankColors.Ink, RoundedCornerShape(4.dp))
                          .padding(2.dp)
                      )
                      Text(
                        text = "BULLETIN REPARASI GANK",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = GankColors.Ink,
                        fontFamily = FontFamily.Monospace
                      )
                    }
                    Text(
                      text = "1. Selalu lakukan pemeriksaan fisik menyeluruh sebelum membuka LCD.",
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      color = GankColors.Ink,
                      modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                      text = "2. Pastikan checklist SESUDAH diselesaikan pada tab 'DETAIL SERVIS' sebelum unit diserahkan kepada konsumen.",
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      color = GankColors.Ink,
                      modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                      text = "3. Gunakan lem khusus T7000/B7000 dan diamkan minimal 30 menit agar merekat presisi.",
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      color = GankColors.Ink
                    )
                  }
                }
              }
            }
          }

          GankTab.DETAIL_SERVIS -> {
            Column(modifier = Modifier.fillMaxSize()) {
              // SEARCH & FILTER HEADER
              NeoBrutalistTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = "Cari Nota / Pelanggan / HP",
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 12.dp),
                trailingIcon = {
                  Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = GankColors.Ink
                  )
                }
              )

              // HORIZONTAL FILTERS
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                val filters = listOf("ALL", "WAITING", "PENGERJAAN", "QC", "SELESAI")
                filters.forEach { filterName ->
                  val isSelected = filterName == selectedFilter
                  Box(
                    modifier = Modifier
                      .background(
                        if (isSelected) GankColors.GankYellow else GankColors.White,
                        RoundedCornerShape(6.dp)
                      )
                      .border(
                        if (isSelected) 3.dp else 2.dp,
                        GankColors.Ink,
                        RoundedCornerShape(6.dp)
                      )
                      .clickable { viewModel.onStatusFilterChanged(filterName) }
                      .padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = filterName,
                      fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                      fontSize = 11.sp,
                      color = GankColors.Ink
                    )
                  }
                }
              }

              // SERVICES LIST TITLE & QUICK ADD
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "ACTIVE REPAIR JOBS (${filteredServices.size})",
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp,
                  color = GankColors.Ink
                )
                Box(
                  modifier = Modifier
                    .background(GankColors.White, RoundedCornerShape(4.dp))
                    .border(2.dp, GankColors.Ink, RoundedCornerShape(4.dp))
                    .clickable { viewModel.openAddJobDialog() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Add,
                      contentDescription = null,
                      modifier = Modifier.size(12.dp),
                      tint = GankColors.Ink
                    )
                    Text("TAMBAH", fontSize = 9.sp, fontWeight = FontWeight.Black)
                  }
                }
              }

              if (filteredServices.isEmpty()) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
                ) {
                  NeoBrutalistCard(
                    backgroundColor = GankColors.White,
                    shadowOffset = 4.dp,
                    borderWidth = 2.dp
                  ) {
                    Column(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                      horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                      Text(
                        text = "TIDAK ADA DATA SERVIS",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = GankColors.Ink,
                        fontFamily = FontFamily.Monospace
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "Silakan tambahkan data baru atau ganti filter pencarian.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = GankColors.Steel
                      )
                    }
                  }
                }
              } else {
                // REPAIR JOBS LAZYCOLUMN
                LazyColumn(
                  modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                  verticalArrangement = Arrangement.spacedBy(16.dp),
                  contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                  items(filteredServices, key = { it.id }) { job ->
                    ServiceJobRow(
                      job = job,
                      onUpdateStatus = { id, stat -> viewModel.updateJobStatus(id, stat) },
                      onDelete = { id -> viewModel.deleteJob(id) },
                      onOpenChecklist = { id, isBefore -> viewModel.openJobChecklistEditor(id, isBefore) }
                    )
                  }
                }
              }
            }
          }

          GankTab.PENGATURAN -> {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
            ) {
              // PENGATURAN BRAND CARD
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 16.dp)
              ) {
                NeoBrutalistCard(
                  backgroundColor = GankColors.White,
                  shadowOffset = 5.dp,
                  borderWidth = 3.dp
                ) {
                  Column(modifier = Modifier.padding(4.dp)) {
                    Text(
                      text = "GANK SERVICE HUB",
                      fontWeight = FontWeight.Black,
                      fontSize = 18.sp,
                      color = GankColors.Ink,
                      fontFamily = FontFamily.Monospace
                    )
                    Text(
                      text = "Lokal & Offline Workshop Management Engine",
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      color = GankColors.Steel,
                      modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Divider(color = GankColors.Ink, modifier = Modifier.padding(bottom = 12.dp))
                    Text(
                      text = "Version: 1.0.0 (Neo-Brutalist Release)",
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      color = GankColors.Ink
                    )
                    Text(
                      text = "Framework: Android SDK, Kotlin, Jetpack Compose",
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      color = GankColors.Ink
                    )
                  }
                }
              }

              // PORTABLE GRADLE ACTIONS & GITHUB CI INFO
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 16.dp)
              ) {
                NeoBrutalistCard(
                  backgroundColor = GankColors.White,
                  shadowOffset = 5.dp,
                  borderWidth = 3.dp
                ) {
                  Column(modifier = Modifier.padding(4.dp)) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp),
                      modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = GankColors.Ink,
                        modifier = Modifier.size(18.dp)
                      )
                      Text(
                        text = "PORTABLE BUILD CONFIG",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = GankColors.Ink,
                        fontFamily = FontFamily.Monospace
                      )
                    }
                    Text(
                      text = "Proyek ini dirancang agar kompatibel dengan GitHub Actions dan runner eksternal standard.",
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      color = GankColors.Steel,
                      modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Box(
                      modifier = Modifier
                        .fillMaxWidth()
                        .background(GankColors.Paper, RoundedCornerShape(4.dp))
                        .border(2.dp, GankColors.Ink, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                    ) {
                      Text(
                        text = "./gradlew assembleDebug\nTarget JDK: 17\nCompile SDK: 34\nBuild Tool: Gradle Kotlin DSL",
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = GankColors.Ink
                      )
                    }
                  }
                }
              }

              // DIAGNOSTICS & HARD RESET SYSTEM
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 20.dp)
              ) {
                NeoBrutalistCard(
                  backgroundColor = GankColors.White,
                  shadowOffset = 5.dp,
                  borderWidth = 3.dp
                ) {
                  Column(modifier = Modifier.padding(4.dp)) {
                    Text(
                      text = "DIAGNOSTIK SISTEM",
                      fontWeight = FontWeight.Black,
                      fontSize = 13.sp,
                      color = GankColors.Ink,
                      modifier = Modifier.padding(bottom = 8.dp),
                      fontFamily = FontFamily.Monospace
                    )
                    Text(
                      text = "Status: Berjalan Normal\nPenyimpanan: Internal Local State",
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      color = GankColors.Steel,
                      modifier = Modifier.padding(bottom = 12.dp)
                    )

                    NeoBrutalistButton(
                      text = "EKSPOR LAPORAN",
                      icon = Icons.Default.Share,
                      containerColor = GankColors.GankYellow,
                      onClick = {
                        // Simulated portable export info
                      },
                      modifier = Modifier.fillMaxWidth()
                    )
                  }
                }
              }
            }
          }
        }
      }

      // BOTTOM NAVIGATION BAR
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 10.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val tabs = listOf(
          Triple(GankTab.DASHBOARD, "DASHBOARD", Icons.Default.Home),
          Triple(GankTab.DETAIL_SERVIS, "DETAIL SERVIS", Icons.Default.List),
          Triple(GankTab.PENGATURAN, "PENGATURAN", Icons.Default.Settings)
        )

        tabs.forEach { (tab, label, icon) ->
          val isSelected = currentTab == tab
          Box(
            modifier = Modifier
              .weight(1f)
              .height(56.dp)
              .background(
                if (isSelected) GankColors.GankYellow else GankColors.White,
                RoundedCornerShape(8.dp)
              )
              .border(
                width = if (isSelected) 3.dp else 2.dp,
                color = GankColors.Ink,
                shape = RoundedCornerShape(8.dp)
              )
              .clickable { currentTab = tab }
              .padding(4.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = icon,
                contentDescription = label,
                tint = GankColors.Ink,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = label,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                fontSize = 9.sp,
                color = GankColors.Ink,
                fontFamily = FontFamily.Monospace,
                lineHeight = 10.sp
              )
            }
          }
        }
      }
    }

    // DIALOG: ADD NEW SERVICE
    if (isAddDialogOpen) {
      AddServiceJobDialog(
        viewModel = viewModel,
        onDismiss = { viewModel.closeAddJobDialog() },
        onSave = { viewModel.addServiceJob() }
      )
    }

    // DIALOG: STANDALONE HARDWARE CHECKLIST
    if (isChecklistDialogOpen) {
      HardwareChecklistDialog(
        viewModel = viewModel,
        onDismiss = { viewModel.closeChecklistDialog() }
      )
    }

    // DIALOG: JOB SPECIFIC BEFORE / AFTER CHECKLIST
    activeJobChecklistId?.let { jobId ->
      val targetJob = services.find { it.id == jobId }
      if (targetJob != null) {
        JobSpecificChecklistDialog(
          job = targetJob,
          isBefore = activeJobChecklistIsBefore,
          onDismiss = { viewModel.closeJobChecklistEditor() },
          onToggleItem = { index -> viewModel.toggleJobChecklistItem(jobId, activeJobChecklistIsBefore, index) }
        )
      }
    }
  }
}

@Composable
fun ServiceJobRow(
  job: ServiceJob,
  onUpdateStatus: (String, String) -> Unit,
  onDelete: (String) -> Unit,
  onOpenChecklist: (String, Boolean) -> Unit
) {
  // Status Badge Colors
  val badgeColor = when (job.status) {
    "WAITING" -> GankColors.Red
    "DIAGNOSA" -> GankColors.Blue
    "PENGERJAAN" -> GankColors.Silver
    "QC" -> GankColors.GankYellow
    "SELESAI", "DIAMBIL" -> GankColors.Green
    else -> GankColors.White
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(IntrinsicSize.Min)
  ) {
    NeoBrutalistCard(
      backgroundColor = GankColors.White,
      shadowOffset = 6.dp,
      borderWidth = 3.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(4.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column {
            Text(
              text = job.hpModel.uppercase(),
              fontWeight = FontWeight.Black,
              fontSize = 18.sp,
              color = GankColors.Ink,
              fontFamily = FontFamily.Monospace
            )
            Text(
              text = "Pelanggan: ${job.customerName}",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = GankColors.Steel
            )
          }
          NeoBrutalistBadge(
            text = job.status,
            containerColor = badgeColor
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Problem Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(GankColors.Paper, RoundedCornerShape(4.dp))
            .border(2.dp, GankColors.Ink, RoundedCornerShape(4.dp))
            .padding(10.dp)
        ) {
          Column {
            Text(
              text = "KELUHAN / KERUSAKAN:",
              fontWeight = FontWeight.Black,
              fontSize = 10.sp,
              color = GankColors.Steel
            )
            Text(
              text = job.problem,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = GankColors.Ink
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // NEW: BEFORE & AFTER CHECKLIST SECTION
        Text(
          text = "QC HARDWARE CHECKLISTS:",
          fontWeight = FontWeight.Black,
          fontSize = 10.sp,
          color = GankColors.Ink,
          modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Sebelum Box
          val beforeOkCount = job.checklistBefore.count { it.isChecked }
          Box(
            modifier = Modifier
              .weight(1f)
              .background(GankColors.Paper, RoundedCornerShape(6.dp))
              .border(2.dp, GankColors.Ink, RoundedCornerShape(6.dp))
              .clickable { onOpenChecklist(job.id, true) }
              .padding(8.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
              Text("SEBELUM SERVIS", fontWeight = FontWeight.Black, fontSize = 9.sp, color = GankColors.Steel)
              Spacer(modifier = Modifier.height(2.dp))
              Text("$beforeOkCount / 12 OK", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = GankColors.Ink)
              Spacer(modifier = Modifier.height(4.dp))
              Text("EDIT CHECKLIST", fontWeight = FontWeight.Bold, fontSize = 8.sp, color = GankColors.Red)
            }
          }

          // Sesudah Box
          val afterOkCount = job.checklistAfter.count { it.isChecked }
          val isReadyForAfter = job.status in listOf("QC", "SELESAI", "DIAMBIL")
          Box(
            modifier = Modifier
              .weight(1f)
              .background(
                if (isReadyForAfter) GankColors.GankYellow else GankColors.White,
                RoundedCornerShape(6.dp)
              )
              .border(2.dp, GankColors.Ink, RoundedCornerShape(6.dp))
              .clickable { onOpenChecklist(job.id, false) }
              .padding(8.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
              Text("SESUDAH SERVIS", fontWeight = FontWeight.Black, fontSize = 9.sp, color = GankColors.Steel)
              Spacer(modifier = Modifier.height(2.dp))
              Text("$afterOkCount / 12 OK", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = GankColors.Ink)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = if (isReadyForAfter) "EDIT CHECKLIST" else "ISI SAAT SELESAI",
                fontWeight = FontWeight.Bold,
                fontSize = 8.sp,
                color = if (isReadyForAfter) GankColors.Ink else GankColors.Steel
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Costs & Date
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "ESTIMASI BIAYA:",
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = GankColors.Steel
            )
            Text(
              text = formatRupiah(job.cost),
              fontWeight = FontWeight.Black,
              fontSize = 16.sp,
              color = GankColors.Ink
            )
          }
          Text(
            text = job.date,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = GankColors.Steel
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Control Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Status Transition buttons
          if (job.status != "SELESAI" && job.status != "DIAMBIL") {
            Button(
              onClick = {
                val nextStatus = when (job.status) {
                  "WAITING" -> "PENGERJAAN"
                  "PENGERJAAN" -> "QC"
                  "QC" -> "SELESAI"
                  else -> "PENGERJAAN"
                }
                onUpdateStatus(job.id, nextStatus)
              },
              colors = ButtonDefaults.buttonColors(containerColor = GankColors.GankYellow),
              border = BorderStroke(2.dp, GankColors.Ink),
              shape = RoundedCornerShape(4.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = "UP STATUS ➔",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = GankColors.Ink
              )
            }
          } else {
            Button(
              onClick = { onUpdateStatus(job.id, "DIAMBIL") },
              colors = ButtonDefaults.buttonColors(containerColor = GankColors.Green),
              border = BorderStroke(2.dp, GankColors.Ink),
              shape = RoundedCornerShape(4.dp),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
              enabled = job.status != "DIAMBIL",
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = if (job.status == "DIAMBIL") "TELAH DIAMBIL ✓" else "AMBIL NOTA ✓",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = GankColors.Ink
              )
            }
          }

          IconButton(
            onClick = { onDelete(job.id) },
            modifier = Modifier
              .border(2.dp, GankColors.Ink, RoundedCornerShape(4.dp))
              .background(GankColors.White)
              .size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Hapus",
              tint = GankColors.Red
            )
          }
        }
      }
    }
  }
}

@Composable
fun AddServiceJobDialog(
  viewModel: GankViewModel,
  onDismiss: () -> Unit,
  onSave: () -> Unit
) {
  val custName by viewModel.inputCustomerName.collectAsState()
  val hpModel by viewModel.inputHpModel.collectAsState()
  val problem by viewModel.inputProblem.collectAsState()
  val cost by viewModel.inputCost.collectAsState()
  val status by viewModel.inputStatus.collectAsState()
  val tempChecklistBefore by viewModel.tempChecklistBefore.collectAsState()

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.9f)
        .background(GankColors.Paper, RoundedCornerShape(12.dp))
        .border(4.dp, GankColors.Ink, RoundedCornerShape(12.dp))
        .padding(16.dp)
    ) {
      val scrollState = rememberScrollState()
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(scrollState)
      ) {
        Text(
          text = "PENERIMAAN SERVIS BARU",
          fontWeight = FontWeight.Black,
          fontSize = 20.sp,
          color = GankColors.Ink,
          modifier = Modifier.padding(bottom = 16.dp),
          fontFamily = FontFamily.Monospace
        )

        NeoBrutalistTextField(
          value = custName,
          onValueChange = { viewModel.inputCustomerName.value = it },
          label = "Nama Pelanggan",
          modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        NeoBrutalistTextField(
          value = hpModel,
          onValueChange = { viewModel.inputHpModel.value = it },
          label = "Model HP",
          modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        NeoBrutalistTextField(
          value = problem,
          onValueChange = { viewModel.inputProblem.value = it },
          label = "Kerusakan / Keluhan",
          modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        NeoBrutalistTextField(
          value = cost,
          onValueChange = { viewModel.inputCost.value = it },
          label = "Estimasi Biaya (Rp)",
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // INTEGRATED DIRECT BEFORE CHECKLIST TOOLKIT
        Text(
          text = "QC SEBELUM SERVIS (RECEIVING CHECK)",
          fontWeight = FontWeight.Black,
          fontSize = 12.sp,
          color = GankColors.Ink,
          modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
          text = "Centang fungsional hardware saat unit HP ini diterima:",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = GankColors.Steel,
          modifier = Modifier.padding(bottom = 10.dp)
        )

        // Checklist Items Grid Layout inside Scrollable Column (6 rows, 2 columns)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .border(2.dp, GankColors.Ink, RoundedCornerShape(8.dp))
            .background(GankColors.White)
            .padding(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val chunkedList = tempChecklistBefore.chunked(2)
          chunkedList.forEachIndexed { rowIndex, pair ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              pair.forEachIndexed { colIndex, item ->
                val actualIndex = rowIndex * 2 + colIndex
                val checked = item.isChecked
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .background(if (checked) GankColors.GankYellow else GankColors.Paper, RoundedCornerShape(6.dp))
                    .border(2.dp, GankColors.Ink, RoundedCornerShape(6.dp))
                    .clickable { viewModel.toggleTempChecklistItem(actualIndex) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                  contentAlignment = Alignment.CenterStart
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(
                      imageVector = if (checked) Icons.Default.Check else Icons.Default.Close,
                      contentDescription = null,
                      tint = if (checked) GankColors.Green else GankColors.Red,
                      modifier = Modifier.size(14.dp)
                    )
                    Text(
                      text = item.name,
                      fontWeight = FontWeight.Black,
                      fontSize = 10.sp,
                      color = GankColors.Ink,
                      lineHeight = 12.sp
                    )
                  }
                }
              }
              if (pair.size < 2) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }

        Text(
          text = "STATUS AWAL",
          fontWeight = FontWeight.Black,
          fontSize = 12.sp,
          color = GankColors.Ink,
          modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val statuses = listOf("WAITING", "PENGERJAAN")
          statuses.forEach { item ->
            val selected = item == status
            Box(
              modifier = Modifier
                .weight(1f)
                .background(if (selected) GankColors.GankYellow else GankColors.White, RoundedCornerShape(6.dp))
                .border(2.dp, GankColors.Ink, RoundedCornerShape(6.dp))
                .clickable { viewModel.inputStatus.value = item }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = item,
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = GankColors.Ink
              )
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          NeoBrutalistButton(
            text = "BATAL",
            containerColor = GankColors.Silver,
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
          )
          NeoBrutalistButton(
            text = "SIMPAN NOTA",
            onClick = onSave,
            modifier = Modifier.weight(1.5f)
          )
        }
      }
    }
  }
}

@Composable
fun HardwareChecklistDialog(
  viewModel: GankViewModel,
  onDismiss: () -> Unit
) {
  val checklist by viewModel.checklist.collectAsState()

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(550.dp)
        .background(GankColors.Paper, RoundedCornerShape(12.dp))
        .border(4.dp, GankColors.Ink, RoundedCornerShape(12.dp))
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Text(
          text = "QC HARDWARE TOOLKIT",
          fontWeight = FontWeight.Black,
          fontSize = 20.sp,
          color = GankColors.Ink,
          fontFamily = FontFamily.Monospace,
          modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
          text = "Checklist standard fungsional HP saat diterima / diserahkan",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = GankColors.Steel,
          modifier = Modifier.padding(bottom = 16.dp)
        )

        // Grid checklist
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          itemsIndexed(checklist) { index, item ->
            val checked = item.isChecked
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(if (checked) GankColors.GankYellow else GankColors.White, RoundedCornerShape(8.dp))
                .border(2.dp, GankColors.Ink, RoundedCornerShape(8.dp))
                .clickable { viewModel.toggleChecklistItem(index) }
                .padding(horizontal = 8.dp, vertical = 4.dp),
              contentAlignment = Alignment.CenterStart
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = if (checked) Icons.Default.Check else Icons.Default.Close,
                  contentDescription = null,
                  tint = if (checked) GankColors.Green else GankColors.Red,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = item.name,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp,
                  color = GankColors.Ink,
                  lineHeight = 13.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          NeoBrutalistButton(
            text = "RESET CHECKLIST",
            containerColor = GankColors.White,
            onClick = { viewModel.resetChecklist() },
            modifier = Modifier.weight(1f)
          )
          NeoBrutalistButton(
            text = "SELESAI",
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}

// NEW: JOB SPECIFIC CHECKLIST DIALOG FOR BEFORE / AFTER SERVICE
@Composable
fun JobSpecificChecklistDialog(
  job: ServiceJob,
  isBefore: Boolean,
  onDismiss: () -> Unit,
  onToggleItem: (Int) -> Unit
) {
  val list = if (isBefore) job.checklistBefore else job.checklistAfter
  val checkedCount = list.count { it.isChecked }

  Dialog(onDismissRequest = onDismiss) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(550.dp)
        .background(GankColors.Paper, RoundedCornerShape(12.dp))
        .border(4.dp, GankColors.Ink, RoundedCornerShape(12.dp))
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        Text(
          text = if (isBefore) "CHECKLIST SEBELUM" else "CHECKLIST SESUDAH",
          fontWeight = FontWeight.Black,
          fontSize = 18.sp,
          color = GankColors.Ink,
          fontFamily = FontFamily.Monospace,
          modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
          text = "${job.hpModel} - Pelanggan: ${job.customerName}",
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp,
          color = GankColors.Steel,
          modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
          text = "Pencatatan fungsional hardware (${checkedCount} / 12 OK)",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = GankColors.Ink,
          modifier = Modifier.padding(bottom = 16.dp)
        )

        // Grid checklist
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          modifier = Modifier.weight(1f),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          itemsIndexed(list) { index, item ->
            val checked = item.isChecked
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(if (checked) GankColors.GankYellow else GankColors.White, RoundedCornerShape(8.dp))
                .border(2.dp, GankColors.Ink, RoundedCornerShape(8.dp))
                .clickable { onToggleItem(index) }
                .padding(horizontal = 8.dp, vertical = 4.dp),
              contentAlignment = Alignment.CenterStart
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = if (checked) Icons.Default.Check else Icons.Default.Close,
                  contentDescription = null,
                  tint = if (checked) GankColors.Green else GankColors.Red,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = item.name,
                  fontWeight = FontWeight.Black,
                  fontSize = 11.sp,
                  color = GankColors.Ink,
                  lineHeight = 13.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        NeoBrutalistButton(
          text = "SIMPAN CHECKLIST",
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

private fun formatRupiah(amount: Long): String {
  val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
  return formatter.format(amount).replace("Rp", "Rp ").replace(",00", "")
}
