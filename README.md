# Horgászbolt
Benne van egy excel file segítségként a pontozáshoz amiben leírtam mi van kész és ha nem triviális a holléte, adtam hozzá elérési útvonalat is.
Emulátorral kaptam néha valamilyen sequel? error-t amitől kicrashelt megnyitáskor, nagy valószínűséggel ez az emulátorom hibája volt, de nem zárnám ki, 
hogy valamit én rontottam el a kódban, az esetek 90%-ában egyébként nem csinálja. 
Ha nem emulátorral tesztelsz akkor légyszi emulátorral tesztelj, mert physical device-al (lehet csak az enyémen) valamiért telepörgeti a db-t adatokkal query-nél.

Bármi kérdésed van v2020.bohus.peter@gmail.com -on elérhető vagyok.

Fordítási hiba nincs	1	nincs	done

Futtatási hiba nincs 1	done

"Firebase autentikáció meg van valósítva: 
Be lehet jelentkezni és regisztrálni"	4	"Regisztráció - 2 pont

Autentikált bejelentkezés - 2 pont"	done

Adatmodell definiálása (class vagy interfész formájában)	2		Nincs kész

Legalább 3 különböző activity használata 	2	done

Beviteli mezők beviteli típusa megfelelő (jelszó kicsillagozva, email-nél megfelelő billentyűzet jelenik meg stb.) 3 - mindenhol meg van valósítva"	done

ConstraintLayout és még egy másik layout típus használata	1	done

"Reszponzív: 
- különböző kijelző méreteken is jól jelennek meg a GUI elemek (akár tableten is)
- elforgatás esetén is igényes marad a layout"	3	"0 - szétesik vagy eltűnnek elemek a layout elforgatása/más méretű kijelző esetén
1 vagy 2 - a layout használható, de nem igényesen változik elfogatás/más méretű kijelző esetén
3 - igényesen meg van valósítva mindenhol"	done

Legalább 2 különböző animáció használata	2	done - mainActivity és shopListactivity

Intentek használata: navigáció meg van valósítva az activityk között (minden activity elérhető)	2	done

"Legalább egy Lifecycle Hook használata a teljes projektben:
- onCreate nem számít
- az alkalmazás funkcionalitásába értelmes módon beágyazott, azaz pl. nem csak egy logolás"	2	done - ShopListActivity.onDestroy()
- 
"Legalább egy olyan androidos erőforrás használata,
amihez kell android permission"	1	done - NotificationJobService kér engedélyt az AndroidManifest.xml-ben

Legalább egy notification vagy alam manager vagy job scheduler használata 	2	done - ShopListActivity.updateAlertIcon()

"CRUD műveletek mindegyike megvalósult és műveletek
service-(ek)be vannak kiszervezve (AsyncTasks)"	5	"műveletenként(C,R,U,D) egy pont,
AsyncTasks használata esetén +1"	done

"Legalább 2 komplex Firestore lekérdezés megvalósítása,
amely indexet igényel (ide tartoznak: where feltétel, rendezés, léptetés, limitálás)"	4	lekérdezésenként 2 pont	done - shoplistactivity - setCartIcon() és SettingsActivity.save()
