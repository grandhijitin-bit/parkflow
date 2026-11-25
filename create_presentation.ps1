# PowerShell script to natively generate 100% compliant, readable PowerPoint slides via PowerPoint COM
Add-Type -AssemblyName System.Drawing

function Color-To-RGB([int]$r, [int]$g, [int]$b) {
    return ($b -shl 16) -bor ($g -shl 8) -bor $r
}

$C_BG_DARK    = Color-To-RGB 15 23 42      # #0F172A
$C_BG_CARD    = Color-To-RGB 30 41 59      # #1E293B
$C_BG_HEADER  = Color-To-RGB 39 53 77      # #27354D
$C_ACCENT     = Color-To-RGB 56 189 248    # #38BDF8 (Cyan)
$C_PRIMARY    = Color-To-RGB 37 99 235     # #2563EB (Royal Blue)
$C_SUCCESS    = Color-To-RGB 16 185 129    # #10B981 (Emerald)
$C_WHITE      = Color-To-RGB 255 255 255   # #FFFFFF
$C_SLATE_HIGH = Color-To-RGB 226 232 240   # #E2E8F0 (High contrast readable text)
$C_BORDER     = Color-To-RGB 51 65 85      # #334155

$ppt = New-Object -ComObject PowerPoint.Application
$pres = $ppt.Presentations.Add([Microsoft.Office.Core.MsoTriState]::msoFalse)

# Standard 16:9 Widescreen (960 x 540 points)
$pres.PageSetup.SlideWidth = 960
$pres.PageSetup.SlideHeight = 540

function Add-StandardSlide([string]$title, [string]$category = "CSE 213 ï¿½ ADVANCED JAVA PROGRAMMING") {
    $slide = $pres.Slides.Add($pres.Slides.Count + 1, 12) # 12 = ppLayoutBlank
    $slide.Background.Fill.Solid()
    $slide.Background.Fill.ForeColor.RGB = $C_BG_DARK

    # Top Tag
    $tbTag = $slide.Shapes.AddTextbox(1, 50, 20, 860, 22)
    $tbTag.TextFrame.TextRange.Text = $category.ToUpper()
    $tbTag.TextFrame.TextRange.Font.Name = "Segoe UI"
    $tbTag.TextFrame.TextRange.Font.Size = 10
    $tbTag.TextFrame.TextRange.Font.Bold = 1
    $tbTag.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT

    # Main Title
    $tbTitle = $slide.Shapes.AddTextbox(1, 50, 38, 860, 40)
    $tbTitle.TextFrame.TextRange.Text = $title
    $tbTitle.TextFrame.TextRange.Font.Name = "Segoe UI"
    $tbTitle.TextFrame.TextRange.Font.Size = 22
    $tbTitle.TextFrame.TextRange.Font.Bold = 1
    $tbTitle.TextFrame.TextRange.Font.Color.RGB = $C_WHITE

    # Divider line
    $div = $slide.Shapes.AddShape(1, 50, 82, 860, 2)
    $div.Fill.Solid()
    $div.Fill.ForeColor.RGB = $C_BORDER
    $div.Line.Visible = 0

    # Footer
    $tbFoot = $slide.Shapes.AddTextbox(1, 50, 508, 860, 20)
    $tbFoot.TextFrame.TextRange.Text = "SRM University ï¿½ AP | Department of CSE | Vehicle Parking Management System (Java & SQLite)"
    $tbFoot.TextFrame.TextRange.Font.Name = "Segoe UI"
    $tbFoot.TextFrame.TextRange.Font.Size = 9
    $tbFoot.TextFrame.TextRange.Font.Color.RGB = $C_SLATE_HIGH

    return $slide
}

# ==============================================================================
# SLIDE 1: STANDOUT MODERN HERO TITLE SLIDE (REDESIGNED)
# ==============================================================================
$s1 = $pres.Slides.Add(1, 12)
$s1.Background.Fill.Solid()
$s1.Background.Fill.ForeColor.RGB = $C_BG_DARK

# Top Vibrant Gradient Accent Line
$accentLine = $s1.Shapes.AddShape(1, 0, 0, 960, 5)
$accentLine.Fill.Solid(); $accentLine.Fill.ForeColor.RGB = $C_ACCENT; $accentLine.Line.Visible = 0

# Category Pill Badge (top left)
$pill = $s1.Shapes.AddShape(1, 50, 20, 370, 24)
$pill.Fill.Solid(); $pill.Fill.ForeColor.RGB = Color-To-RGB 24 38 68
$pill.Line.ForeColor.RGB = $C_ACCENT; $pill.Line.Weight = 1

$ptb = $s1.Shapes.AddTextbox(1, 50, 20, 370, 24)
$ptb.TextFrame.TextRange.Text = "CSE 213 ï¿½ ADVANCED JAVA PROGRAMMING ï¿½ CAPSTONE"
$ptb.TextFrame.TextRange.Font.Name = "Segoe UI"
$ptb.TextFrame.TextRange.Font.Size = 9
$ptb.TextFrame.TextRange.Font.Bold = 1
$ptb.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT
$ptb.TextFrame.TextRange.ParagraphFormat.Alignment = 2 # Center

# Modern Logo Icon Box: "[ P ]"
$logoBox = $s1.Shapes.AddShape(1, 50, 54, 52, 52)
$logoBox.Fill.Solid(); $logoBox.Fill.ForeColor.RGB = $C_PRIMARY
$logoBox.Line.ForeColor.RGB = $C_ACCENT; $logoBox.Line.Weight = 2

$logoTxt = $s1.Shapes.AddTextbox(1, 50, 54, 52, 52)
$logoTxt.TextFrame.TextRange.Text = "P"
$logoTxt.TextFrame.TextRange.Font.Name = "Segoe UI"
$logoTxt.TextFrame.TextRange.Font.Size = 28
$logoTxt.TextFrame.TextRange.Font.Bold = 1
$logoTxt.TextFrame.TextRange.Font.Color.RGB = $C_WHITE
$logoTxt.TextFrame.TextRange.ParagraphFormat.Alignment = 2 # Center

# Hero Title
$mainTitle = $s1.Shapes.AddTextbox(1, 115, 48, 800, 38)
$mtRange = $mainTitle.TextFrame.TextRange
$mtRange.Text = "Vehicle Parking Management System"
$mtRange.Font.Name = "Segoe UI"
$mtRange.Font.Size = 26
$mtRange.Font.Bold = 1
$mtRange.Font.Color.RGB = $C_WHITE

# Subtitle
$subTitle = $s1.Shapes.AddTextbox(1, 118, 85, 800, 24)
$stRange = $subTitle.TextFrame.TextRange
$stRange.Text = "Standalone Desktop Application with Java Swing, Multithreading & SQLite Persistence"
$stRange.Font.Name = "Segoe UI"
$stRange.Font.Size = 12
$stRange.Font.Color.RGB = $C_SLATE_HIGH

# 4 Tech Stack Pills
$techs = @("Java 17+ Modern JVM", "Java Swing Dark UI", "Transactional SQLite", "AutoSave Thread (30s)")
$txX = 118
foreach ($t in $techs) {
    $tBadge = $s1.Shapes.AddShape(1, $txX, 114, 138, 20)
    $tBadge.Fill.Solid(); $tBadge.Fill.ForeColor.RGB = Color-To-RGB 30 45 74
    $tBadge.Line.ForeColor.RGB = $C_BORDER; $tBadge.Line.Weight = 1
    
    $tbTxt = $s1.Shapes.AddTextbox(1, $txX, 114, 138, 20)
    $tbTxt.TextFrame.TextRange.Text = $t
    $tbTxt.TextFrame.TextRange.Font.Name = "Segoe UI"
    $tbTxt.TextFrame.TextRange.Font.Size = 8.5
    $tbTxt.TextFrame.TextRange.Font.Bold = 1
    $tbTxt.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT
    $tbTxt.TextFrame.TextRange.ParagraphFormat.Alignment = 2
    $txX += 144
}

# Horizontal Section Divider
$secDiv = $s1.Shapes.AddShape(1, 50, 146, 860, 1)
$secDiv.Fill.Solid(); $secDiv.Fill.ForeColor.RGB = $C_BORDER; $secDiv.Line.Visible = 0

# Left Section: Team Members 2x2 Grid
$teamHeader = $s1.Shapes.AddTextbox(1, 50, 154, 550, 20)
$teamHeader.TextFrame.TextRange.Text = "PROJECT DEVELOPMENT TEAM"
$teamHeader.TextFrame.TextRange.Font.Name = "Segoe UI"
$teamHeader.TextFrame.TextRange.Font.Size = 10
$teamHeader.TextFrame.TextRange.Font.Bold = 1
$teamHeader.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT

$membersData = @(
    @{ name="Chandrala Rishit Prem"; id="AP25110011030"; role="System Architecture & OOP Framework"; tag="Lead Architect" },
    @{ name="M. Hiranya Kumar"; id="AP25110011019"; role="Core Services & Multithreading Engine"; tag="Concurrency & Logic" },
    @{ name="K. Komal"; id="AP25110011040"; role="Java Swing UI & Database Engineering"; tag="UI / UX & SQLite" },
    @{ name="G.V.N.S.S. Jitin"; id="AP25110010958"; role="Automated Testing & Documentation"; tag="QA & Validation" }
)

$gridPositions = @(
    @{ x=50;  y=178 },
    @{ x=330; y=178 },
    @{ x=50;  y=310 },
    @{ x=330; y=310 }
)

for ($i = 0; $i -lt 4; $i++) {
    $pos = $gridPositions[$i]
    $m = $membersData[$i]
    
    # Card
    $card = $s1.Shapes.AddShape(1, $pos.x, $pos.y, 265, 120)
    $card.Fill.Solid(); $card.Fill.ForeColor.RGB = $C_BG_CARD
    $card.Line.ForeColor.RGB = $C_BORDER; $card.Line.Weight = 1
    
    # Left accent indicator strip
    $strip = $s1.Shapes.AddShape(1, $pos.x, $pos.y, 4, 120)
    $strip.Fill.Solid(); $strip.Fill.ForeColor.RGB = $C_ACCENT; $strip.Line.Visible = 0
    
    # Role Badge
    $tagShape = $s1.Shapes.AddShape(1, $pos.x + 12, $pos.y + 10, 110, 17)
    $tagShape.Fill.Solid(); $tagShape.Fill.ForeColor.RGB = Color-To-RGB 20 40 70
    $tagShape.Line.Visible = 0
    $tagTb = $s1.Shapes.AddTextbox(1, $pos.x + 12, $pos.y + 10, 110, 17)
    $tagTb.TextFrame.TextRange.Text = $m.tag.ToUpper()
    $tagTb.TextFrame.TextRange.Font.Name = "Segoe UI"
    $tagTb.TextFrame.TextRange.Font.Size = 7.5
    $tagTb.TextFrame.TextRange.Font.Bold = 1
    $tagTb.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT
    $tagTb.TextFrame.TextRange.ParagraphFormat.Alignment = 2
    
    # Student Name
    $nTb = $s1.Shapes.AddTextbox(1, $pos.x + 10, $pos.y + 30, 245, 24)
    $nTb.TextFrame.TextRange.Text = $m.name
    $nTb.TextFrame.TextRange.Font.Name = "Segoe UI"
    $nTb.TextFrame.TextRange.Font.Size = 12
    $nTb.TextFrame.TextRange.Font.Bold = 1
    $nTb.TextFrame.TextRange.Font.Color.RGB = $C_WHITE
    
    # Reg ID
    $idTb = $s1.Shapes.AddTextbox(1, $pos.x + 10, $pos.y + 54, 245, 20)
    $idTb.TextFrame.TextRange.Text = $m.id
    $idTb.TextFrame.TextRange.Font.Name = "Consolas"
    $idTb.TextFrame.TextRange.Font.Size = 10.5
    $idTb.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT
    
    # Responsibility
    $rTb = $s1.Shapes.AddTextbox(1, $pos.x + 10, $pos.y + 74, 245, 38)
    $rTb.TextFrame.TextRange.Text = $m.role
    $rTb.TextFrame.TextRange.Font.Name = "Segoe UI"
    $rTb.TextFrame.TextRange.Font.Size = 9.5
    $rTb.TextFrame.TextRange.Font.Color.RGB = $C_SLATE_HIGH
}

# Right Section: Elevated Academic & Supervisor Card
$supCard = $s1.Shapes.AddShape(1, 615, 178, 295, 252)
$supCard.Fill.Solid(); $supCard.Fill.ForeColor.RGB = Color-To-RGB 24 33 50
$supCard.Line.ForeColor.RGB = $C_PRIMARY; $supCard.Line.Weight = 1.5

$supH = $s1.Shapes.AddTextbox(1, 630, 188, 265, 20)
$supH.TextFrame.TextRange.Text = "PROJECT SUPERVISOR"
$supH.TextFrame.TextRange.Font.Name = "Segoe UI"
$supH.TextFrame.TextRange.Font.Size = 10
$supH.TextFrame.TextRange.Font.Bold = 1
$supH.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT

$supN = $s1.Shapes.AddTextbox(1, 630, 208, 265, 62)
$supNR = $supN.TextFrame.TextRange
$supNR.Font.Name = "Segoe UI"
$supNR.Text = @"
Dr. Prakash Ankur
Assistant Professor, Department of CSE
School of Engineering & Applied Sciences
"@
$supNR.Font.Size = 10.5
$supNR.Font.Color.RGB = $C_WHITE

# Divider inside supervisor card
$cDiv = $s1.Shapes.AddShape(1, 630, 276, 265, 1)
$cDiv.Fill.Solid(); $cDiv.Fill.ForeColor.RGB = $C_BORDER; $cDiv.Line.Visible = 0

$instH = $s1.Shapes.AddTextbox(1, 630, 286, 265, 20)
$instH.TextFrame.TextRange.Text = "INSTITUTION & PROGRAM"
$instH.TextFrame.TextRange.Font.Name = "Segoe UI"
$instH.TextFrame.TextRange.Font.Size = 10
$instH.TextFrame.TextRange.Font.Bold = 1
$instH.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT

$instN = $s1.Shapes.AddTextbox(1, 630, 308, 265, 95)
$instNR = $instN.TextFrame.TextRange
$instNR.Font.Name = "Segoe UI"
$instNR.Text = @"
SRM University ï¿½ AP, Andhra Pradesh
B.Tech Computer Science & Engineering
III Semester ï¿½ Section B
Academic Year: 2026 ï¿½ 2027 (Odd Sem)
"@
$instNR.Font.Size = 10.5
$instNR.Font.Color.RGB = $C_WHITE

# Bottom Ribbon
$botRibbon = $s1.Shapes.AddShape(1, 0, 535, 960, 5)
$botRibbon.Fill.Solid(); $botRibbon.Fill.ForeColor.RGB = $C_PRIMARY; $botRibbon.Line.Visible = 0


# ==============================================================================
# SLIDE 2: TABLE OF CONTENTS (AGENDA & NAVIGATION)
# ==============================================================================
$sTOC = Add-StandardSlide "Table of Contents & Presentation Outline" "Agenda"

$tocItems = @(
    @{ no="01"; title="Executive Summary"; desc="Industry Challenges, Objectives & Solution Architecture" },
    @{ no="02"; title="System Architecture & OOP"; desc="Layered Architecture, Inheritance, Polymorphism & Abstraction" },
    @{ no="03"; title="Core Java Concepts & APIs"; desc="Collections Framework, EnumMap, Java Streams & DateTime API" },
    @{ no="04"; title="Business Rules & Pricing"; desc="Slot Sizing, Fixed Hourly Tariff & Ceiling Billing Algorithm" },
    @{ no="05"; title="Database Persistence Layer"; desc="SQLite Embedded Database, Schema Design & JDBC Integration" },
    @{ no="06"; title="Lifecycle & Robustness"; desc="System State Flow, Custom Exceptions & Error Recovery" },
    @{ no="07"; title="Multithreading & Concurrency"; desc="Swing EDT, 30s Background Auto-Save Daemon & Volatile Flags" },
    @{ no="08"; title="User Interface Modules"; desc="Live Dashboard, Vehicle Park/Exit Panels & Parking History" },
    @{ no="09"; title="Testing & Verification"; desc="Automated Test Suite, Edge Case Verification & Passed Suites" },
    @{ no="10"; title="Team Matrix & Roadmap"; desc="Member Contributions, Industry Extensions (ANPR) & Conclusion" }
)

# Render 2 columns x 5 rows grid
$colWidth = 420
$rowHeight = 68
$startY = 102
$startX1 = 50
$startX2 = 490

for ($idx = 0; $idx -lt $tocItems.Count; $idx++) {
    $item = $tocItems[$idx]
    $col = [int]($idx -ge 5)
    $row = $idx % 5
    
    $posX = if ($col -eq 0) { $startX1 } else { $startX2 }
    $posY = $startY + ($row * ($rowHeight + 8))
    
    # Background Card
    $card = $sTOC.Shapes.AddShape(1, $posX, $posY, $colWidth, $rowHeight)
    $card.Fill.Solid()
    $card.Fill.ForeColor.RGB = $C_BG_CARD
    $card.Line.ForeColor.RGB = $C_BORDER
    $card.Line.Weight = 1
    
    # Left accent badge with number
    $badge = $sTOC.Shapes.AddShape(1, $posX, $posY, 46, $rowHeight)
    $badge.Fill.Solid()
    $badge.Fill.ForeColor.RGB = Color-To-RGB 24 38 68
    $badge.Line.Visible = 0
    
    $noTb = $sTOC.Shapes.AddTextbox(1, $posX, $posY + 16, 46, 32)
    $noTb.TextFrame.TextRange.Text = $item.no
    $noTb.TextFrame.TextRange.Font.Name = "Consolas"
    $noTb.TextFrame.TextRange.Font.Size = 14
    $noTb.TextFrame.TextRange.Font.Bold = 1
    $noTb.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT
    $noTb.TextFrame.TextRange.ParagraphFormat.Alignment = 2 # Center
    
    # Content Title and Desc
    $tTb = $sTOC.Shapes.AddTextbox(1, $posX + 54, $posY + 6, $colWidth - 62, 26)
    $tTb.TextFrame.TextRange.Text = $item.title
    $tTb.TextFrame.TextRange.Font.Name = "Segoe UI"
    $tTb.TextFrame.TextRange.Font.Size = 11.5
    $tTb.TextFrame.TextRange.Font.Bold = 1
    $tTb.TextFrame.TextRange.Font.Color.RGB = $C_WHITE

    $dTb = $sTOC.Shapes.AddTextbox(1, $posX + 54, $posY + 32, $colWidth - 62, 30)
    $dTb.TextFrame.TextRange.Text = $item.desc
    $dTb.TextFrame.TextRange.Font.Name = "Segoe UI"
    $dTb.TextFrame.TextRange.Font.Size = 9.5
    $dTb.TextFrame.TextRange.Font.Color.RGB = $C_SLATE_HIGH
}


# ==========================================
# SLIDE 3: PROBLEM STATEMENT & MOTIVATION
# ==========================================
$s2 = Add-StandardSlide "Problem Statement & Industry Context" "Executive Summary"

$challenges = @(
    @{ n="01"; t="Slot Collisions"; d="Manual parking leads to occupied slots getting allotted again due to lack of synchronized slot tracking." },
    @{ n="02"; t="Revenue Leakage"; d="Attendants round duration arbitrarily, resulting in billing inaccuracies and lost parking revenue." },
    @{ n="03"; t="Slow Retrieval"; d="Searching paper records or flat logs delays exit processing during peak parking hours (O(N) search)." },
    @{ n="04"; t="No Visibility"; d="Lack of visual slot matrices prevents attendants from knowing category-specific capacity (Bike/Car/Truck)." }
)

$cx = 50
foreach ($ch in $challenges) {
    $card = $s2.Shapes.AddShape(1, $cx, 105, 200, 240)
    $card.Fill.Solid(); $card.Fill.ForeColor.RGB = $C_BG_CARD; $card.Line.ForeColor.RGB = $C_BORDER; $card.Line.Weight = 1

    $tb = $s2.Shapes.AddTextbox(1, $cx + 12, 115, 176, 220)
    $r = $tb.TextFrame.TextRange
    $r.Font.Name = "Segoe UI"
    $r.Text = "$($ch.n)`r`n`r`n$($ch.t)`r`n`r`n$($ch.d)"
    $r.Font.Size = 11
    $r.Font.Color.RGB = $C_WHITE
    $cx += 220
}

# Solution Card
$sol = $s2.Shapes.AddShape(1, 50, 365, 860, 120)
$sol.Fill.Solid(); $sol.Fill.ForeColor.RGB = $C_PRIMARY; $sol.Line.ForeColor.RGB = $C_ACCENT; $sol.Line.Weight = 1.5

$solTb = $s2.Shapes.AddTextbox(1, 65, 375, 830, 100)
$solR = $solTb.TextFrame.TextRange
$solR.Font.Name = "Segoe UI"
$solR.Text = @"
OUR SOLUTION: STANDALONE JAVA DESKTOP SYSTEM
An independent, zero-server desktop application running on Java Swing and SQLite.
ï¿½ Automates category-specific slot allocation and issues unique tickets
ï¿½ Calculates fees deterministically using the ceiling rule (minimum 1 hour)
ï¿½ Ensures O(1) active vehicle retrieval with HashMaps
ï¿½ Safeguards data with multithreaded background auto-save every 30 seconds
"@
$solR.Font.Size = 11.5
$solR.Font.Color.RGB = $C_WHITE

# ==========================================
# SLIDE 3: SYSTEM ARCHITECTURE
# ==========================================
$s3 = Add-StandardSlide "Clean 7-Package Layered Architecture" "Software Architecture"

$tbl3 = $s3.Shapes.AddTable(8, 3, 50, 105, 860, 380)
$t3Data = @(
    @("Package", "Primary Responsibility", "Core Classes & Interfaces"),
    @("app", "Application bootstrap, L&F setup, launches Swing on EDT", "Main.java"),
    @("model", "Business domain entities, encapsulation & polymorphic hierarchy", "Billable, Vehicle, Bike, Car, Truck, ParkingSlot, ParkingTicket, Enums"),
    @("service", "Central coordinator, tariff billing engine & background auto-save", "ParkingManager, BillingService, AutoSaveService"),
    @("database", "SQLite JDBC connectivity, transactions, prepared statements", "DatabaseManager.java"),
    @("exceptions", "Custom application exceptions for domain validation & error states", "InvalidVehicleException, DuplicateVehicleException, ParkingFullException, VehicleNotFoundException"),
    @("ui", "Dark-themed Swing UI, sidebar navigation, custom cards & tables", "Theme, MainFrame, DashboardPanel, ParkVehiclePanel, ExitVehiclePanel, HistoryPanel, ReceiptDialog"),
    @("util", "Formatting & regex validation utilities", "DateTimeUtil, ValidationUtil")
)

$tbl3.Table.Columns.Item(1).Width = 110
$tbl3.Table.Columns.Item(2).Width = 400
$tbl3.Table.Columns.Item(3).Width = 350

for ($row = 1; $row -le 8; $row++) {
    for ($col = 1; $col -le 3; $col++) {
        $c = $tbl3.Table.Cell($row, $col)
        $c.Shape.TextFrame.TextRange.Text = $t3Data[$row-1][$col-1]
        $c.Shape.TextFrame.TextRange.Font.Name = "Segoe UI"
        $c.Shape.TextFrame.TextRange.Font.Size = 10
        if ($row -eq 1) {
            $c.Shape.Fill.Solid(); $c.Shape.Fill.ForeColor.RGB = $C_BG_HEADER
            $c.Shape.TextFrame.TextRange.Font.Bold = 1
            $c.Shape.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT
        } else {
            $c.Shape.Fill.Solid(); $c.Shape.Fill.ForeColor.RGB = $C_BG_CARD
            $c.Shape.TextFrame.TextRange.Font.Color.RGB = $C_WHITE
        }
    }
}

# ==========================================
# SLIDE 4: OBJECT-ORIENTED PROGRAMMING
# ==========================================
$s4 = Add-StandardSlide "Object-Oriented Programming (OOP) Architecture" "Advanced Java Core"

$oopItems = @(
    @{ t="1. INTERFACE: Billable"; d="Defines the billing contract for parking operations.`r`n`r`nContract Method:`r`nï¿½ calculateFee(entryTime, exitTime, hourlyRate)`r`n`r`nKey Benefits:`r`nï¿½ Establishes loose coupling`r`nï¿½ Allows future extensions (EV charging, valet) without altering existing vehicle logic." },
    @{ t="2. ABSTRACT CLASS: Vehicle"; d="Encapsulates common attributes & behavior:`r`nï¿½ private final String vehicleNumber`r`nï¿½ private final String ownerName`r`nï¿½ private final VehicleType type`r`n`r`nAbstract Method for Polymorphism:`r`nï¿½ getVehicleCategoryDescription()`r`n`r`nCommon Behavior:`r`nï¿½ Implements default ceiling fee calculation`r`nï¿½ Overrides equals() and hashCode() based on normalized registration number." },
    @{ t="3. RUNTIME POLYMORPHISM"; d="Concrete Subclasses of Vehicle:`r`n`r`nï¿½ Bike (Two-Wheeler)`r`n  Overrides description:`r`n  'Two-Wheeler (Motorcycle / Scooter)'`r`n`r`nï¿½ Car (Four-Wheeler)`r`n  Overrides description:`r`n  'Four-Wheeler (Sedan / SUV / Hatchback)'`r`n`r`nï¿½ Truck (Heavy Vehicle)`r`n  Overrides description:`r`n  'Heavy Vehicle (Commercial Truck / Bus)'`r`n`r`nPolymorphic binding occurs dynamically during check-in." }
)

$ox = 50
foreach ($item in $oopItems) {
    $card = $s4.Shapes.AddShape(1, $ox, 105, 275, 380)
    $card.Fill.Solid(); $card.Fill.ForeColor.RGB = $C_BG_CARD; $card.Line.ForeColor.RGB = $C_BORDER; $card.Line.Weight = 1

    $tb = $s4.Shapes.AddTextbox(1, $ox + 12, 115, 250, 360)
    $r = $tb.TextFrame.TextRange
    $r.Font.Name = "Segoe UI"
    $r.Text = "$($item.t)`r`n`r`n$($item.d)"
    $r.Font.Size = 10.5
    $r.Font.Color.RGB = $C_WHITE
    $ox += 292
}

# ==========================================
# SLIDE 5: DATA STRUCTURES & MODERN JAVA
# ==========================================
$s5 = Add-StandardSlide "Collections & Modern Java 8+ APIs in Action" "Data Structures & APIs"

$dsItems = @(
    @{ t="HashMap for O(1) Lookups"; d="ï¿½ activeTicketsByVehicle: Instantly verifies duplicates & retrieves active tickets by registration number.`r`nï¿½ activeTicketsById: Enables instant barcode/ticket-ID searches.`r`nï¿½ slotsById: Provides O(1) slot inquiries without DB roundtrips." },
    @{ t="EnumMap for Category Tariffs"; d="ï¿½ EnumMap<VehicleType, Double> in BillingService.`r`nï¿½ Backed by internal arrays; offers extreme cache locality, type safety, and higher performance than standard HashMap." },
    @{ t="Java Streams & Lambdas"; d="ï¿½ Slot Allocation:`r`n  slots.stream().filter(s -> s.getType() == type && s.isAvailable()).findFirst()`r`nï¿½ History Search: Multi-predicate stream pipeline`r`nï¿½ Revenue Sum: allTickets.stream().mapToDouble().sum()" },
    @{ t="Java Date/Time API (java.time)"; d="ï¿½ Uses LocalDateTime and Duration instead of deprecated java.util.Date.`r`nï¿½ Accurately measures elapsed seconds for ceiling division.`r`nï¿½ DateTimeFormatter guarantees consistent display and ISO storage." }
)

$dx = 50; $dy = 105
for ($i = 0; $i -lt 4; $i++) {
    $card = $s5.Shapes.AddShape(1, $dx, $dy, 420, 180)
    $card.Fill.Solid(); $card.Fill.ForeColor.RGB = $C_BG_CARD; $card.Line.ForeColor.RGB = $C_BORDER; $card.Line.Weight = 1

    $tb = $s5.Shapes.AddTextbox(1, $dx + 12, $dy + 10, 396, 160)
    $r = $tb.TextFrame.TextRange
    $r.Font.Name = "Segoe UI"
    $r.Text = "$($dsItems[$i].t)`r`n`r`n$($dsItems[$i].d)"
    $r.Font.Size = 10.5
    $r.Font.Color.RGB = $C_WHITE

    if ($i % 2 -eq 0) { $dx = 490 } else { $dx = 50; $dy += 195 }
}

# ==========================================
# SLIDE 6: BILLING ENGINE
# ==========================================
$s6 = Add-StandardSlide "Deterministic Billing Engine & Ceiling Rule" "Business Rules"

$bCard = $s6.Shapes.AddShape(1, 50, 105, 390, 380)
$bCard.Fill.Solid(); $bCard.Fill.ForeColor.RGB = $C_BG_CARD; $bCard.Line.ForeColor.RGB = $C_BORDER; $bCard.Line.Weight = 1

$bTb = $s6.Shapes.AddTextbox(1, 65, 115, 360, 360)
$bR = $bTb.TextFrame.TextRange
$bR.Font.Name = "Segoe UI"
$bR.Text = @"
Fixed Tariff Specifications

ï¿½ Hourly Rate: Fixed at ?50.00 / hour for all vehicle types.
ï¿½ Minimum Billing: 1 hour minimum fee (?50.00) even for stays < 1 minute.
ï¿½ Ceiling Rule: Any partial hour rounds up to the next full hour.
ï¿½ Failure Safety: Failed/incomplete operations are never charged.

Mathematical Formula:
  totalSeconds = Duration.between(entry, exit).getSeconds();
  billedHours  = Math.max(1, (long) Math.ceil(totalSeconds / 3600.0));
  totalFee     = billedHours * hourlyRate;
"@
$bR.Font.Size = 11
$bR.Font.Color.RGB = $C_WHITE

$tbl6 = $s6.Shapes.AddTable(6, 4, 460, 105, 450, 380)
$t6Data = @(
    @("Actual Stay", "Billed Hours", "Fee (INR)", "Rule Applied"),
    @("1 minute", "1 hour", "?50.00", "Minimum 1-hour rule"),
    @("60 minutes", "1 hour", "?50.00", "Exact hour boundary"),
    @("61 minutes", "2 hours", "?100.00", "Ceiling partial-hour rule"),
    @("130 minutes", "3 hours", "?150.00", "2h 10m rounded up to 3h"),
    @("5 hours", "5 hours", "?250.00", "Full multi-hour tariff")
)

$tbl6.Table.Columns.Item(1).Width = 100
$tbl6.Table.Columns.Item(2).Width = 90
$tbl6.Table.Columns.Item(3).Width = 90
$tbl6.Table.Columns.Item(4).Width = 170

for ($row = 1; $row -le 6; $row++) {
    for ($col = 1; $col -le 4; $col++) {
        $c = $tbl6.Table.Cell($row, $col)
        $c.Shape.TextFrame.TextRange.Text = $t6Data[$row-1][$col-1]
        $c.Shape.TextFrame.TextRange.Font.Name = "Segoe UI"
        $c.Shape.TextFrame.TextRange.Font.Size = 10.5
        if ($row -eq 1) {
            $c.Shape.Fill.Solid(); $c.Shape.Fill.ForeColor.RGB = $C_BG_HEADER
            $c.Shape.TextFrame.TextRange.Font.Bold = 1
            $c.Shape.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT
        } else {
            $c.Shape.Fill.Solid(); $c.Shape.Fill.ForeColor.RGB = $C_BG_CARD
            if ($col -eq 3) {
                $c.Shape.TextFrame.TextRange.Font.Bold = 1
                $c.Shape.TextFrame.TextRange.Font.Color.RGB = $C_SUCCESS
            } else {
                $c.Shape.TextFrame.TextRange.Font.Color.RGB = $C_WHITE
            }
        }
    }
}

# ==========================================
# SLIDE 7: DATABASE DESIGN
# ==========================================
$s7 = Add-StandardSlide "Database Architecture & SQLite Schema" "Persistence Layer"

$dbItems = @(
    @{ t="Table: slots (Physical Capacity)"; d="ï¿½ id (TEXT PRIMARY KEY): Slot ID (B-01..B-10, C-01..C-15, T-01..T-05)`r`nï¿½ type (TEXT): BIKE, CAR, TRUCK`r`nï¿½ occupied (INTEGER): 0 = Vacant, 1 = Occupied`r`nï¿½ vehicle_no (TEXT): Parked registration or NULL" },
    @{ t="Table: tickets (Parking Sessions)"; d="ï¿½ id (TEXT PRIMARY KEY): Unique ID (PK-C...)`r`nï¿½ vehicle_no (TEXT): Registration number`r`nï¿½ owner_name (TEXT): Driver full name`r`nï¿½ type (TEXT): Vehicle type`r`nï¿½ slot_id (TEXT): Allocated slot reference`r`nï¿½ entry_time (TEXT): ISO-8601 timestamp`r`nï¿½ exit_time (TEXT): Timestamp (NULL while active)`r`nï¿½ fee (REAL): Total billed fee (INR)" },
    @{ t="Table: rates (Tariff Configuration)"; d="ï¿½ type (TEXT PRIMARY KEY): Vehicle type`r`nï¿½ rate (REAL): Hourly rate (?50 default)`r`n`r`nACID & Transaction Features:`r`nï¿½ PRAGMA foreign_keys = ON & WAL mode`r`nï¿½ Batch execution with commit/rollback`r`nï¿½ Try-with-resources connection safety" }
)

$sx = 50
foreach ($item in $dbItems) {
    $card = $s7.Shapes.AddShape(1, $sx, 105, 275, 380)
    $card.Fill.Solid(); $card.Fill.ForeColor.RGB = $C_BG_CARD; $card.Line.ForeColor.RGB = $C_BORDER; $card.Line.Weight = 1

    $tb = $s7.Shapes.AddTextbox(1, $sx + 12, 115, 250, 360)
    $r = $tb.TextFrame.TextRange
    $r.Font.Name = "Segoe UI"
    $r.Text = "$($item.t)`r`n`r`n$($item.d)"
    $r.Font.Size = 10.5
    $r.Font.Color.RGB = $C_WHITE
    $sx += 292
}

# ==========================================
# SLIDE 8: END-TO-END WORKFLOW
# ==========================================
$s8 = Add-StandardSlide "End-to-End Operational Workflow" "System Flow"

$steps = @(
    @{ s="1. Launch & Seeding"; d="Main.java initializes SQLite; auto-seeds 30 slots & rates if empty; restores memory state." },
    @{ s="2. Check-In (Park)"; d="Validates registration regex; checks duplicate HashMap; allocates first free matching slot; generates ticket." },
    @{ s="3. Check-Out (Exit)"; d="Attendant searches by Vehicle No or Ticket ID (O(1)); computes duration, ceiling hours & fee." },
    @{ s="4. Receipt & Release"; d="Prompts exit confirmation; releases slot (marks vacant); generates official tax invoice receipt." },
    @{ s="5. History & Safe Exit"; d="Provides multi-predicate stream search; safe application close terminates background thread and flushes state." }
)

$sy = 105
foreach ($step in $steps) {
    $c = $s8.Shapes.AddShape(1, 50, $sy, 860, 68)
    $c.Fill.Solid(); $c.Fill.ForeColor.RGB = $C_BG_CARD; $c.Line.ForeColor.RGB = $C_BORDER; $c.Line.Weight = 1

    $b = $s8.Shapes.AddShape(1, 60, $sy + 10, 190, 48)
    $b.Fill.Solid(); $b.Fill.ForeColor.RGB = $C_PRIMARY; $b.Line.ForeColor.RGB = $C_ACCENT; $b.Line.Weight = 1

    $btb = $s8.Shapes.AddTextbox(1, 60, $sy + 18, 190, 30)
    $btb.TextFrame.TextRange.Text = $step.s
    $btb.TextFrame.TextRange.Font.Name = "Segoe UI"
    $btb.TextFrame.TextRange.Font.Size = 11
    $btb.TextFrame.TextRange.Font.Bold = 1
    $btb.TextFrame.TextRange.Font.Color.RGB = $C_WHITE
    $btb.TextFrame.TextRange.ParagraphFormat.Alignment = 2 # Center

    $dtb = $s8.Shapes.AddTextbox(1, 265, $sy + 15, 630, 40)
    $dtb.TextFrame.TextRange.Text = $step.d
    $dtb.TextFrame.TextRange.Font.Name = "Segoe UI"
    $dtb.TextFrame.TextRange.Font.Size = 11
    $dtb.TextFrame.TextRange.Font.Color.RGB = $C_WHITE

    $sy += 78
}

# ==========================================
# SLIDE 9: CUSTOM EXCEPTIONS
# ==========================================
$s9 = Add-StandardSlide "Custom Exceptions & Defensive Programming" "Robustness"

$tbl9 = $s9.Shapes.AddTable(5, 3, 50, 105, 860, 380)
$t9Data = @(
    @("Custom Exception Class", "Trigger Condition", "System Recovery & User Feedback"),
    @("InvalidVehicleException", "Malformed registration number or blank owner name", "Rejects input with warning modal; focuses back on input field without crashing."),
    @("DuplicateVehicleException", "Vehicle registration is already parked in an active slot", "Displays existing Ticket ID and occupied slot number; blocks second entry."),
    @("ParkingFullException", "All category slots (Bikes, Cars, or Trucks) are occupied", "Alerts attendant that category capacity is exhausted; offers alternative advice."),
    @("VehicleNotFoundException", "Queried vehicle number or Ticket ID does not exist in active pool", "Displays clear lookup failure error modal; preserves existing input for correction.")
)

$tbl9.Table.Columns.Item(1).Width = 210
$tbl9.Table.Columns.Item(2).Width = 320
$tbl9.Table.Columns.Item(3).Width = 330

for ($row = 1; $row -le 5; $row++) {
    for ($col = 1; $col -le 3; $col++) {
        $c = $tbl9.Table.Cell($row, $col)
        $c.Shape.TextFrame.TextRange.Text = $t9Data[$row-1][$col-1]
        $c.Shape.TextFrame.TextRange.Font.Name = "Segoe UI"
        $c.Shape.TextFrame.TextRange.Font.Size = 10.5
        if ($row -eq 1) {
            $c.Shape.Fill.Solid(); $c.Shape.Fill.ForeColor.RGB = $C_BG_HEADER
            $c.Shape.TextFrame.TextRange.Font.Bold = 1
            $c.Shape.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT
        } else {
            $c.Shape.Fill.Solid(); $c.Shape.Fill.ForeColor.RGB = $C_BG_CARD
            $c.Shape.TextFrame.TextRange.Font.Color.RGB = $C_WHITE
        }
    }
}

# ==========================================
# SLIDE 10: MULTITHREADING
# ==========================================
$s10 = Add-StandardSlide "Multithreading, Concurrency & Data Safety" "Multithreading Architecture"

$m1 = $s10.Shapes.AddShape(1, 50, 105, 420, 380)
$m1.Fill.Solid(); $m1.Fill.ForeColor.RGB = $C_BG_CARD; $m1.Line.ForeColor.RGB = $C_BORDER; $m1.Line.Weight = 1

$mtb1 = $s10.Shapes.AddTextbox(1, 65, 115, 390, 360)
$mr1 = $mtb1.TextFrame.TextRange
$mr1.Font.Name = "Segoe UI"
$mr1.Text = @"
Background AutoSaveService

ï¿½ Dedicated Background Thread:
  Implements Runnable, runs as a daemon thread named 'ParkingSystem-AutoSave-Thread'.

ï¿½ 30-Second Periodic Interval:
  Executes periodic SQLite flushes without locking the user interface.

ï¿½ volatile boolean running:
  Guarantees memory visibility across threads for thread-safe shutdown.

ï¿½ InterruptedException Handling:
  Cleanly terminates thread when application window closes.
"@
$mr1.Font.Size = 11
$mr1.Font.Color.RGB = $C_WHITE

$m2 = $s10.Shapes.AddShape(1, 490, 105, 420, 380)
$m2.Fill.Solid(); $m2.Fill.ForeColor.RGB = $C_BG_CARD; $m2.Line.ForeColor.RGB = $C_BORDER; $m2.Line.Weight = 1

$mtb2 = $s10.Shapes.AddTextbox(1, 505, 115, 390, 360)
$mr2 = $mtb2.TextFrame.TextRange
$mr2.Font.Name = "Segoe UI"
$mr2.Text = @"
Thread Safety & Swing EDT Non-Blocking

ï¿½ Synchronized Critical Sections:
  Methods in ParkingManager (parkVehicle, exitVehicle, savePendingData) are synchronized to eliminate race conditions between user actions and auto-save.

ï¿½ Event Dispatch Thread (EDT) Isolation:
  UI event listeners hand off database operations safely, preventing GUI freezing.

ï¿½ SwingUtilities.invokeLater():
  Ensures all GUI mutations and live clocks run exclusively on the EDT.
"@
$mr2.Font.Size = 11
$mr2.Font.Color.RGB = $C_WHITE

# ==========================================
# SLIDE 11: UI: DASHBOARD & PARK
# ==========================================
$s11 = Add-StandardSlide "Desktop UI: Dashboard & Park Vehicle (Check-In)" "User Interface"

$u1 = $s11.Shapes.AddShape(1, 50, 100, 420, 390)
$u1.Fill.Solid(); $u1.Fill.ForeColor.RGB = $C_BG_CARD; $u1.Line.ForeColor.RGB = $C_BORDER; $u1.Line.Weight = 1

$utb1 = $s11.Shapes.AddTextbox(1, 60, 105, 400, 75)
$ur1 = $utb1.TextFrame.TextRange
$ur1.Font.Name = "Segoe UI"
$ur1.Text = @"
Live System Dashboard
- 6 Live Statistics Cards: 30 slots capacity, entries, exits, revenue
- Dynamic 30-Slot Grid Matrix: Visual green/red slot states
"@
$ur1.Font.Size = 10
$ur1.Font.Color.RGB = $C_WHITE

# Add Dashboard Screenshot
$dashPic = $s11.Shapes.AddPicture("$PSScriptRoot\screenshots\dashboard.png", [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 60, 185, 400, 290)
$dashPic.Line.ForeColor.RGB = $C_ACCENT
$dashPic.Line.Weight = 1

$u2 = $s11.Shapes.AddShape(1, 490, 100, 420, 390)
$u2.Fill.Solid(); $u2.Fill.ForeColor.RGB = $C_BG_CARD; $u2.Line.ForeColor.RGB = $C_BORDER; $u2.Line.Weight = 1

$utb2 = $s11.Shapes.AddTextbox(1, 500, 105, 400, 75)
$ur2 = $utb2.TextFrame.TextRange
$ur2.Font.Name = "Segoe UI"
$ur2.Text = @"
Park Vehicle (Check-In) Panel
- Form Input: Registration number, driver name, vehicle category
- Automated Slot Allocation: Next free category slot (e.g. C-01)
"@
$ur2.Font.Size = 10
$ur2.Font.Color.RGB = $C_WHITE

# Add Park Vehicle Screenshot
$parkPic = $s11.Shapes.AddPicture("$PSScriptRoot\screenshots\park_vehicle.png", [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 500, 185, 400, 290)
$parkPic.Line.ForeColor.RGB = $C_ACCENT
$parkPic.Line.Weight = 1

# ==========================================
# SLIDE 12: UI: EXIT & RECEIPT
# ==========================================
$s12 = Add-StandardSlide "Desktop UI: Exit Vehicle (Check-Out) & Invoice Receipt" "User Interface"

$e1 = $s12.Shapes.AddShape(1, 50, 100, 520, 390)
$e1.Fill.Solid(); $e1.Fill.ForeColor.RGB = $C_BG_CARD; $e1.Line.ForeColor.RGB = $C_BORDER; $e1.Line.Weight = 1

$etb1 = $s12.Shapes.AddTextbox(1, 60, 105, 500, 75)
$er1 = $etb1.TextFrame.TextRange
$er1.Font.Name = "Segoe UI"
$er1.Text = @"
Exit Vehicle (Check-Out) Panel
- Registration/Ticket Lookup: Instant O(1) active vehicle retrieval
- Ceiling Tariff Calculation: Automated duration & fee (Rs 50/hr minimum)
"@
$er1.Font.Size = 10
$er1.Font.Color.RGB = $C_WHITE

# Add Exit Vehicle Screenshot
$exitPic = $s12.Shapes.AddPicture("$PSScriptRoot\screenshots\exit_vehicle.png", [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 60, 185, 500, 290)
$exitPic.Line.ForeColor.RGB = $C_ACCENT
$exitPic.Line.Weight = 1

$e2 = $s12.Shapes.AddShape(1, 590, 100, 320, 390)
$e2.Fill.Solid(); $e2.Fill.ForeColor.RGB = $C_BG_CARD; $e2.Line.ForeColor.RGB = $C_BORDER; $e2.Line.Weight = 1

$etb2 = $s12.Shapes.AddTextbox(1, 600, 105, 300, 60)
$er2 = $etb2.TextFrame.TextRange
$er2.Font.Name = "Segoe UI"
$er2.Text = @"
Official Invoice Receipt
- Modal Tax Invoice Dialog: Total paid highlighted
"@
$er2.Font.Size = 10
$er2.Font.Color.RGB = $C_WHITE

# Add Receipt Screenshot
$receiptPic = $s12.Shapes.AddPicture("$PSScriptRoot\screenshots\receipt_dialog.png", [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 615, 170, 270, 305)
$receiptPic.Line.ForeColor.RGB = $C_ACCENT
$receiptPic.Line.Weight = 1

# ==========================================
# SLIDE 13: UI: PARKING HISTORY & AUDIT
# ==========================================
$s13 = Add-StandardSlide "Desktop UI: Parking History & Audit Stream" "User Interface"

$h1 = $s13.Shapes.AddShape(1, 50, 100, 860, 390)
$h1.Fill.Solid(); $h1.Fill.ForeColor.RGB = $C_BG_CARD; $h1.Line.ForeColor.RGB = $C_BORDER; $h1.Line.Weight = 1

$htb1 = $s13.Shapes.AddTextbox(1, 65, 105, 830, 65)
$hr1 = $htb1.TextFrame.TextRange
$hr1.Font.Name = "Segoe UI"
$hr1.Text = @"
Completed Sessions & Audit Trail
- Real-time search by registration number, ticket ID, or driver name with date filtering
- Filtered Revenue Aggregation calculated via Java Streams API
"@
$hr1.Font.Size = 10.5
$hr1.Font.Color.RGB = $C_WHITE

# Add Parking History Screenshot
$historyPic = $s13.Shapes.AddPicture("$PSScriptRoot\screenshots\parking_history.png", [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoTrue, 65, 175, 830, 300)
$historyPic.Line.ForeColor.RGB = $C_ACCENT
$historyPic.Line.Weight = 1
# ==========================================
# SLIDE 14: TESTING & VERIFICATION
# ==========================================
$s14 = Add-StandardSlide "Rigorous Verification: 11 Automated Test Suites" "Testing & Verification"

$tbl14 = $s14.Shapes.AddTable(12, 4, 50, 105, 860, 380)
$t14Data = @(
    @("Test #", "Test Suite Name", "Business Rule / Technical Concept Verified", "Result"),
    @("T01", "testBillingRules", "1min=?50, 60min=?50, 61min=?100, 130min=?150, 5h=?250", "PASSED [?]"),
    @("T02", "testSuccessfulRegistration", "Slot prefix allocation (PK-C.. -> C-01) & active state", "PASSED [?]"),
    @("T03", "testInvalidVehicleNumber", "Rejection of blank / illegal characters (InvalidVehicleException)", "PASSED [?]"),
    @("T04", "testDuplicateActiveVehicle", "Rejection of already-parked active vehicle (DuplicateVehicleException)", "PASSED [?]"),
    @("T05", "testParkingFullException", "Exhaustion of 5 Truck slots triggers ParkingFullException", "PASSED [?]"),
    @("T06", "testSuccessfulExit", "Slot release, exit timestamp setting & active flag clearing", "PASSED [?]"),
    @("T07", "testUnknownVehicleLookup", "Throws VehicleNotFoundException when record not found", "PASSED [?]"),
    @("T08", "testRevenueCalculation", "Cumulative revenue calculation using Stream.mapToDouble()", "PASSED [?]"),
    @("T09", "testHistoryFiltering", "Stream multi-predicate search by vehicle, owner & date", "PASSED [?]"),
    @("T10", "testPersistenceAcrossRestart", "SQLite persistence reload verified across fresh instances", "PASSED [?]"),
    @("T11", "testAutoSaveAndShutdown", "AutoSaveService daemon thread & volatile running flag shutdown", "PASSED [?]")
)

$tbl14.Table.Columns.Item(1).Width = 60
$tbl14.Table.Columns.Item(2).Width = 240
$tbl14.Table.Columns.Item(3).Width = 450
$tbl14.Table.Columns.Item(4).Width = 110

for ($row = 1; $row -le 12; $row++) {
    for ($col = 1; $col -le 4; $col++) {
        $c = $tbl14.Table.Cell($row, $col)
        $c.Shape.TextFrame.TextRange.Text = $t14Data[$row-1][$col-1]
        $c.Shape.TextFrame.TextRange.Font.Name = "Segoe UI"
        $c.Shape.TextFrame.TextRange.Font.Size = 9.5
        if ($row -eq 1) {
            $c.Shape.Fill.Solid(); $c.Shape.Fill.ForeColor.RGB = $C_BG_HEADER
            $c.Shape.TextFrame.TextRange.Font.Bold = 1
            $c.Shape.TextFrame.TextRange.Font.Color.RGB = $C_ACCENT
        } else {
            $c.Shape.Fill.Solid(); $c.Shape.Fill.ForeColor.RGB = $C_BG_CARD
            if ($col -eq 4) {
                $c.Shape.TextFrame.TextRange.Font.Bold = 1
                $c.Shape.TextFrame.TextRange.Font.Color.RGB = $C_SUCCESS
            } else {
                $c.Shape.TextFrame.TextRange.Font.Color.RGB = $C_WHITE
            }
        }
    }
}

# ==========================================
# SLIDE 15: TEAM CONTRIBUTIONS
# ==========================================
$s15 = Add-StandardSlide "Individual Contributions & Ownership" "Team Project Matrix"

$contribs = @(
    @{ n="Chandrala Rishit Prem"; id="AP25110011030"; r="System Architecture & OOP Framework"; d="ï¿½ Designed Billable interface, Vehicle abstract hierarchy, and concrete subclasses.`r`nï¿½ Implemented custom exception classes and regex validation rules.`r`nï¿½ Formulated domain model encapsulation and slot allocation algorithms." },
    @{ n="M. Hiranya Kumar"; id="AP25110011019"; r="Core Services, Multithreading & Billing"; d="ï¿½ Developed central ParkingManager coordinator and BillingService ceiling engine.`r`nï¿½ Implemented AutoSaveService daemon thread with volatile execution control.`r`nï¿½ Managed synchronized blocks to prevent concurrency race conditions." },
    @{ n="K. Komal"; id="AP25110011040"; r="Java Swing UI & Database Engineering"; d="ï¿½ Designed modern dark-theme Swing interface, cards, and 30-slot visual matrix.`r`nï¿½ Engineered SQLite schema, prepared statements, and transactional batch updates.`r`nï¿½ Created printable ReceiptDialog modal and live system clock." },
    @{ n="G.V.N.S.S. Jitin"; id="AP25110010958"; r="Automated Testing, Build & Packaging"; d="ï¿½ Developed comprehensive TestParkingSystem suite verifying all 11 test specifications.`r`nï¿½ Configured Maven pom.xml and standalone Windows batch execution scripts.`r`nï¿½ Documented user workflows and prepared presentation deliverables." }
)

$cx = 50
foreach ($m in $contribs) {
    $card = $s15.Shapes.AddShape(1, $cx, 105, 200, 380)
    $card.Fill.Solid(); $card.Fill.ForeColor.RGB = $C_BG_CARD; $card.Line.ForeColor.RGB = $C_BORDER; $card.Line.Weight = 1

    $tb = $s15.Shapes.AddTextbox(1, $cx + 10, 115, 180, 360)
    $r = $tb.TextFrame.TextRange
    $r.Font.Name = "Segoe UI"
    $r.Text = "$($m.n)`r`n$($m.id)`r`n`r`n$($m.r)`r`n`r`n$($m.d)"
    $r.Font.Size = 9.5
    $r.Font.Color.RGB = $C_WHITE
    $cx += 220
}

# ==========================================
# SLIDE 16: CONCLUSION
# ==========================================
$s16 = Add-StandardSlide "Conclusion, Key Takeaways & Future Scope" "Project Conclusion"

$con1 = $s16.Shapes.AddShape(1, 50, 105, 420, 380)
$con1.Fill.Solid(); $con1.Fill.ForeColor.RGB = $C_BG_CARD; $con1.Line.ForeColor.RGB = $C_BORDER; $con1.Line.Weight = 1

$ctb1 = $s16.Shapes.AddTextbox(1, 65, 115, 390, 360)
$cr1 = $ctb1.TextFrame.TextRange
$cr1.Font.Name = "Segoe UI"
$cr1.Text = @"
Academic & Technical Takeaways

ï¿½ Practical mastery of Advanced Java OOP:
  Polymorphism, abstract classes, interfaces, and clean package separation.

ï¿½ Effective Collections & Streams Integration:
  HashMap for instant O(1) lookups, EnumMap for category rates, and Streams for multi-predicate audit filtering.

ï¿½ Resilient Concurrency & Memory Safety:
  Background daemon threads, volatile execution flags, and synchronized data persistence.

ï¿½ Zero-Server Durability:
  Transactional SQLite JDBC with automatic startup initialization.
"@
$cr1.Font.Size = 11
$cr1.Font.Color.RGB = $C_WHITE

$con2 = $s16.Shapes.AddShape(1, 490, 105, 420, 380)
$con2.Fill.Solid(); $con2.Fill.ForeColor.RGB = $C_BG_CARD; $con2.Line.ForeColor.RGB = $C_BORDER; $con2.Line.Weight = 1

$ctb2 = $s16.Shapes.AddTextbox(1, 505, 115, 390, 360)
$cr2 = $ctb2.TextFrame.TextRange
$cr2.Font.Name = "Segoe UI"
$cr2.Text = @"
Future Roadmap & Industry Extensions

ï¿½ Computer Vision (ANPR):
  Automatic Number Plate Recognition via optical camera feeds at entry/exit gates.

ï¿½ IoT Hardware Sensors:
  Ultrasonic barrier sensors to automatically open gate arms upon ticket issuance.

ï¿½ Digital Payments Integration:
  Dynamic UPI QR codes printed directly on exit receipts.

ï¿½ Multi-Gate Enterprise Deployment:
  Migrating embedded SQLite to centralized MySQL server for synchronized multi-lane parking facilities.
"@
$cr2.Font.Size = 11
$cr2.Font.Color.RGB = $C_WHITE

# Save presentation natively through PowerPoint
$outPath = (Resolve-Path '.').Path + '\Vehicle_Parking_Management_System_Presentation.pptx'

# Delete old file if present
if (Test-Path $outPath) {
    Remove-Item -Force $outPath
}

$pres.SaveAs($outPath)
$pres.Close()
$ppt.Quit()
[System.Runtime.Interopservices.Marshal]::ReleaseComObject($ppt) | Out-Null

Write-Output "[SUCCESS] Native presentation created at: $outPath"
