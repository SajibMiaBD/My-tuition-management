package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.util.AppLanguage

enum class AppTheme(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val descBn: String,
    val descEn: String,
    val previewPrimary: Color,
    val previewSecondary: Color,
    val previewAccent: Color,
    val previewBackground: Color,
    val isDarkLuxury: Boolean = false
) {
    ROYAL_EMERALD(
        id = "royal_emerald",
        nameBn = "রাজকীয় পান্না (Royal Emerald)",
        nameEn = "Royal Emerald",
        descBn = "গভীর পান্না সবুজ ও আভিজাত্যময় স্বর্ণাভ ক্লাসিক রূপ",
        descEn = "Deep imperial emerald green & champagne gold prestige",
        previewPrimary = Color(0xFF064E3B),
        previewSecondary = Color(0xFFD97706),
        previewAccent = Color(0xFF10B981),
        previewBackground = Color(0xFFF7FAF8)
    ),

    SAPPHIRE_PRESTIGE(
        id = "sapphire_prestige",
        nameBn = "নীলকান্তমণি (Sapphire Prestige)",
        nameEn = "Sapphire Prestige",
        descBn = "অভিজাত রয়্যাল স্যাফায়ার ও সুইস ব্যাংকিং ব্লু",
        descEn = "Regal sapphire navy & Swiss banking elegance",
        previewPrimary = Color(0xFF1E3A8A),
        previewSecondary = Color(0xFF0284C7),
        previewAccent = Color(0xFF38BDF8),
        previewBackground = Color(0xFFF6F9FD)
    ),

    AMETHYST_ROYAL(
        id = "amethyst_royal",
        nameBn = "রাজকীয় আম্বর (Amethyst Royal)",
        nameEn = "Amethyst Royal",
        descBn = "দামী রত্নপাথরের গভীর বেগুনি ও ল্যাভেন্ডার আভা",
        descEn = "Imperial gemstone violet & lavender glow",
        previewPrimary = Color(0xFF581C87),
        previewSecondary = Color(0xFF9333EA),
        previewAccent = Color(0xFFC084FC),
        previewBackground = Color(0xFFFAF7FD)
    ),

    MIDNIGHT_OBSIDIAN(
        id = "midnight_obsidian",
        nameBn = "মিডনাইট অমাবস্যা (Midnight Obsidian)",
        nameEn = "Midnight Obsidian (OLED)",
        descBn = "ব্ল্যাক কার্ড লাক্সারি, ওলেড ডার্ক ও গোল্ডেন গ্লো",
        descEn = "Black-card luxury OLED pitch black & radiant gold",
        previewPrimary = Color(0xFFF59E0B),
        previewSecondary = Color(0xFF38BDF8),
        previewAccent = Color(0xFFE2E8F0),
        previewBackground = Color(0xFF0A0D12),
        isDarkLuxury = true
    ),

    ROSE_GOLD_PRESTIGE(
        id = "rose_gold_prestige",
        nameBn = "রোজ গোল্ড (Rose Gold Prestige)",
        nameEn = "Rose Gold Prestige",
        descBn = "উষ্ণ শ্যাম্পেন, রোজ গোল্ড ও সিল্ক পার্ল আভা",
        descEn = "Warm champagne, rose gold & silk pearl elegance",
        previewPrimary = Color(0xFF881337),
        previewSecondary = Color(0xFFCA8A04),
        previewAccent = Color(0xFFFB7185),
        previewBackground = Color(0xFFFFF7F7)
    ),

    CRIMSON_HERITAGE(
        id = "crimson_heritage",
        nameBn = "ক্রিমসন হেরিটেজ (Crimson Heritage)",
        nameEn = "Crimson Heritage",
        descBn = "ঐতিহাসিক অক্সফোর্ড বার্গান্ডি ও অ্যান্টিক ব্রাস",
        descEn = "Classic Oxford burgundy velvet & antique brass",
        previewPrimary = Color(0xFF7F1D1D),
        previewSecondary = Color(0xFFB45309),
        previewAccent = Color(0xFFEF4444),
        previewBackground = Color(0xFFFDFBF7)
    ),

    NORDIC_SLATE(
        id = "nordic_slate",
        nameBn = "নর্ডিক স্লেট (Nordic Slate)",
        nameEn = "Nordic Slate",
        descBn = "স্ক্যান্ডিনেভিয়ান মিনিমাল পেট্রোল ও বরফ নীল",
        descEn = "Scandinavian matte slate petrol & ice teal minimalism",
        previewPrimary = Color(0xFF134E4A),
        previewSecondary = Color(0xFF0F766E),
        previewAccent = Color(0xFF2DD4BF),
        previewBackground = Color(0xFFF4F7F6)
    ),

    GOLDEN_SUNSET(
        id = "golden_sunset",
        nameBn = "সানসেট গোল্ডেন (Sunset Amber)",
        nameEn = "Sunset Amber",
        descBn = "উষ্ণ জাফরান সোনা, মধু অ্যাম্বার ও এসপ্রেসো",
        descEn = "Warm saffron gold, honey amber & espresso luxury",
        previewPrimary = Color(0xFFB45309),
        previewSecondary = Color(0xFF78350F),
        previewAccent = Color(0xFFF59E0B),
        previewBackground = Color(0xFFFDFCF9)
    ),

    TITANIUM_EXECUTIVE(
        id = "titanium_executive",
        nameBn = "এক্সিকিউটিভ টাইটানিয়াম (Executive Titanium)",
        nameEn = "Executive Titanium",
        descBn = "হাই-কনট্রাস্ট মেটালিক গ্রাফাইট ও কোবাল্ট আধুনিকতা",
        descEn = "High-contrast metallic graphite & cobalt precision",
        previewPrimary = Color(0xFF1E293B),
        previewSecondary = Color(0xFF2563EB),
        previewAccent = Color(0xFF64748B),
        previewBackground = Color(0xFFF8FAFC)
    ),

    OCEANIC_LUXE(
        id = "oceanic_luxe",
        nameBn = "অ্যাকোয়ামেরিন ওশান (Oceanic Luxe)",
        nameEn = "Oceanic Luxe",
        descBn = "গভীর মহাসাগরের নীল ও উজ্জ্বল অ্যাকোয়ামেরিন",
        descEn = "Deep marine abyss & radiant turquoise brilliance",
        previewPrimary = Color(0xFF0E7490),
        previewSecondary = Color(0xFF0284C7),
        previewAccent = Color(0xFF06B6D4),
        previewBackground = Color(0xFFF0FDF4)
    );

    fun displayName(lang: AppLanguage): String = if (lang == AppLanguage.BN) nameBn else nameEn
    fun description(lang: AppLanguage): String = if (lang == AppLanguage.BN) descBn else descEn

    companion object {
        fun fromName(name: String?): AppTheme {
            return entries.firstOrNull { it.name.equals(name, ignoreCase = true) || it.id.equals(name, ignoreCase = true) }
                ?: ROYAL_EMERALD
        }
    }
}

object ThemeFactory {

    fun getColorScheme(theme: AppTheme): ColorScheme {
        return when (theme) {
            AppTheme.ROYAL_EMERALD -> lightColorScheme(
                primary = Color(0xFF064E3B),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFD1FAE5),
                onPrimaryContainer = Color(0xFF022C22),
                secondary = Color(0xFFD97706),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFFEF3C7),
                onSecondaryContainer = Color(0xFF451A03),
                tertiary = Color(0xFF047857),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFECFDF5),
                onTertiaryContainer = Color(0xFF064E3B),
                background = Color(0xFFF7FAF8),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFECFDF5),
                onBackground = Color(0xFF1E292B),
                onSurface = Color(0xFF1E292B),
                onSurfaceVariant = Color(0xFF4B5563),
                outline = Color(0xFFA7F3D0)
            )

            AppTheme.SAPPHIRE_PRESTIGE -> lightColorScheme(
                primary = Color(0xFF1E3A8A),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFDBEAFE),
                onPrimaryContainer = Color(0xFF172554),
                secondary = Color(0xFF0284C7),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFE0F2FE),
                onSecondaryContainer = Color(0xFF082F49),
                tertiary = Color(0xFF0369A1),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFF0F9FF),
                onTertiaryContainer = Color(0xFF0C4A6E),
                background = Color(0xFFF6F9FD),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFEFF6FF),
                onBackground = Color(0xFF0F172A),
                onSurface = Color(0xFF0F172A),
                onSurfaceVariant = Color(0xFF475569),
                outline = Color(0xFFBFDBFE)
            )

            AppTheme.AMETHYST_ROYAL -> lightColorScheme(
                primary = Color(0xFF581C87),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFF3E8FF),
                onPrimaryContainer = Color(0xFF3B0764),
                secondary = Color(0xFF9333EA),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFFAF5FF),
                onSecondaryContainer = Color(0xFF581C87),
                tertiary = Color(0xFF7E22CE),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFF3E8FF),
                onTertiaryContainer = Color(0xFF3B0764),
                background = Color(0xFFFAF7FD),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFF5F3FF),
                onBackground = Color(0xFF2E1065),
                onSurface = Color(0xFF2E1065),
                onSurfaceVariant = Color(0xFF6B21A8),
                outline = Color(0xFFDDD6FE)
            )

            AppTheme.MIDNIGHT_OBSIDIAN -> darkColorScheme(
                primary = Color(0xFFF59E0B),
                onPrimary = Color(0xFF090D12),
                primaryContainer = Color(0xFF292524),
                onPrimaryContainer = Color(0xFFFDE68A),
                secondary = Color(0xFF38BDF8),
                onSecondary = Color(0xFF090D12),
                secondaryContainer = Color(0xFF1E293B),
                onSecondaryContainer = Color(0xFFBAE6FD),
                tertiary = Color(0xFFA855F7),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFF3B0764),
                onTertiaryContainer = Color(0xFFE9D5FF),
                background = Color(0xFF090D12),
                surface = Color(0xFF111722),
                surfaceVariant = Color(0xFF1B2230),
                onBackground = Color(0xFFF1F5F9),
                onSurface = Color(0xFFF1F5F9),
                onSurfaceVariant = Color(0xFF94A3B8),
                outline = Color(0xFF334155)
            )

            AppTheme.ROSE_GOLD_PRESTIGE -> lightColorScheme(
                primary = Color(0xFF881337),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFFE4E6),
                onPrimaryContainer = Color(0xFF4C0519),
                secondary = Color(0xFFCA8A04),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFFEF9C3),
                onSecondaryContainer = Color(0xFF713F12),
                tertiary = Color(0xFFE11D48),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFFFF1F2),
                onTertiaryContainer = Color(0xFF881337),
                background = Color(0xFFFFF7F7),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFFFF1F2),
                onBackground = Color(0xFF370817),
                onSurface = Color(0xFF370817),
                onSurfaceVariant = Color(0xFF701A33),
                outline = Color(0xFFFECDD3)
            )

            AppTheme.CRIMSON_HERITAGE -> lightColorScheme(
                primary = Color(0xFF7F1D1D),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFEE2E2),
                onPrimaryContainer = Color(0xFF450A0A),
                secondary = Color(0xFFB45309),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFFFEDD5),
                onSecondaryContainer = Color(0xFF7C2D12),
                tertiary = Color(0xFF991B1B),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFFEF2F2),
                onTertiaryContainer = Color(0xFF7F1D1D),
                background = Color(0xFFFDFBF7),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFFEF2F2),
                onBackground = Color(0xFF2D0C0C),
                onSurface = Color(0xFF2D0C0C),
                onSurfaceVariant = Color(0xFF631818),
                outline = Color(0xFFFECACA)
            )

            AppTheme.NORDIC_SLATE -> lightColorScheme(
                primary = Color(0xFF134E4A),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFCCFBF1),
                onPrimaryContainer = Color(0xFF042F2E),
                secondary = Color(0xFF0F766E),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFE6FFFA),
                onSecondaryContainer = Color(0xFF115E59),
                tertiary = Color(0xFF14B8A6),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFF0FDFA),
                onTertiaryContainer = Color(0xFF0F766E),
                background = Color(0xFFF4F7F6),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE2EBE8),
                onBackground = Color(0xFF132A29),
                onSurface = Color(0xFF132A29),
                onSurfaceVariant = Color(0xFF3D5A58),
                outline = Color(0xFF99F6E4)
            )

            AppTheme.GOLDEN_SUNSET -> lightColorScheme(
                primary = Color(0xFFB45309),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFEF3C7),
                onPrimaryContainer = Color(0xFF451A03),
                secondary = Color(0xFF78350F),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFFFFBEB),
                onSecondaryContainer = Color(0xFF451A03),
                tertiary = Color(0xFFD97706),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFFEF3C7),
                onTertiaryContainer = Color(0xFF78350F),
                background = Color(0xFFFDFCF9),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFFEF3C7),
                onBackground = Color(0xFF291505),
                onSurface = Color(0xFF291505),
                onSurfaceVariant = Color(0xFF5E3917),
                outline = Color(0xFFFDE68A)
            )

            AppTheme.TITANIUM_EXECUTIVE -> lightColorScheme(
                primary = Color(0xFF1E293B),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE2E8F0),
                onPrimaryContainer = Color(0xFF0F172A),
                secondary = Color(0xFF2563EB),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFDBEAFE),
                onSecondaryContainer = Color(0xFF1E40AF),
                tertiary = Color(0xFF475569),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFF1F5F9),
                onTertiaryContainer = Color(0xFF1E293B),
                background = Color(0xFFF8FAFC),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFF1F5F9),
                onBackground = Color(0xFF0F172A),
                onSurface = Color(0xFF0F172A),
                onSurfaceVariant = Color(0xFF475569),
                outline = Color(0xFFCBD5E1)
            )

            AppTheme.OCEANIC_LUXE -> lightColorScheme(
                primary = Color(0xFF0E7490),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFCFFAFE),
                onPrimaryContainer = Color(0xFF164E63),
                secondary = Color(0xFF0284C7),
                onSecondary = Color.White,
                secondaryContainer = Color(0xFFE0F2FE),
                onSecondaryContainer = Color(0xFF0369A1),
                tertiary = Color(0xFF0891B2),
                onTertiary = Color.White,
                tertiaryContainer = Color(0xFFECFEFF),
                onTertiaryContainer = Color(0xFF155E75),
                background = Color(0xFFF0FDF4),
                surface = Color(0xFFFFFFFF),
                surfaceVariant = Color(0xFFE0F2FE),
                onBackground = Color(0xFF082F49),
                onSurface = Color(0xFF082F49),
                onSurfaceVariant = Color(0xFF0369A1),
                outline = Color(0xFFA5F3FC)
            )
        }
    }

    fun getTypography(theme: AppTheme): Typography {
        val headerFont = when (theme) {
            AppTheme.ROYAL_EMERALD,
            AppTheme.AMETHYST_ROYAL,
            AppTheme.ROSE_GOLD_PRESTIGE,
            AppTheme.CRIMSON_HERITAGE -> FontFamily.Serif

            AppTheme.MIDNIGHT_OBSIDIAN,
            AppTheme.TITANIUM_EXECUTIVE -> FontFamily.SansSerif

            AppTheme.SAPPHIRE_PRESTIGE,
            AppTheme.NORDIC_SLATE,
            AppTheme.GOLDEN_SUNSET,
            AppTheme.OCEANIC_LUXE -> FontFamily.Default
        }

        val tracking = when (theme) {
            AppTheme.ROYAL_EMERALD,
            AppTheme.AMETHYST_ROYAL,
            AppTheme.CRIMSON_HERITAGE -> 0.3.sp

            AppTheme.MIDNIGHT_OBSIDIAN,
            AppTheme.TITANIUM_EXECUTIVE -> (-0.2).sp

            else -> 0.sp
        }

        return Typography(
            headlineLarge = TextStyle(
                fontFamily = headerFont,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                letterSpacing = tracking
            ),
            headlineMedium = TextStyle(
                fontFamily = headerFont,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                letterSpacing = tracking
            ),
            titleLarge = TextStyle(
                fontFamily = headerFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = tracking
            ),
            titleMedium = TextStyle(
                fontFamily = headerFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                lineHeight = 24.sp,
                letterSpacing = tracking
            ),
            titleSmall = TextStyle(
                fontFamily = headerFont,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp
            ),
            bodyLarge = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.3.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp
            ),
            bodySmall = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp
            ),
            labelLarge = TextStyle(
                fontFamily = headerFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp
            ),
            labelMedium = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}
