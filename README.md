# SYM - Labo 3

Auteurs : Benoît Schopfer, Antoine Rochat et Jérémie Châtillon

## 2.4) Question

```
Dans la manipulation ci-dessus, les tags NFC utilisés contiennent 4 valeurs textuelles codées en UTF-8 dans un format de message NDEF. Une personne malveillante ayant accès au porte-clés peut aisément copier les valeurs stockées dans celui-ci et les répliquer sur une autre puce NFC. 

A partir de l’API Android concernant les tags NFC4, pouvez-vous imaginer une autre approche pour rendre plus compliqué le clonage des tags NFC ? Est-ce possible sur toutes les plateformes (Android et iOS), existe-il des limitations ? Voyez-vous d’autres possibilités ? 
```

Pour empêcher la lecture des données d'un tag NFC, on peut crypter les données et/ou utiliser une norme propriétaire. Ainsi, un attaquant pourra lire le tag mais ne pourra pas déchiffrer ses données.

En revanche, cela ne l'empêche pas de duppliquer le tag. Pour empêcher cela, on peut utiliser des tags ne pouvant pas être ré-écrits et vérifier l'identifiant unique du tag afin d'être certain qu'il s'agit de notre tag et non pas d'une copie. En effet, cet identifiant unique est programmé par le constructeur, unique à chaque tag et ne peut pas être modifié.

## 3.2) Question

```
Comparer la technologie à codes-barres et la technologie NFC, du point de vue d'une utilisation dans des applications pour smartphones, dans une optique : 
• Professionnelle (Authentification, droits d’accès, stockage d’une clé) 
• Grand public (Billetterie, contrôle d’accès, e-paiement) 
• Ludique (Preuves d'achat, publicité, etc.) 
• Financier (Coûts pour le déploiement de la technologie, possibilités de recyclage, etc.) 
```

#### Professionnelle :

Dans un cadre professionnel, la technologie NFC est généralement plus appropriée. Dans le cadre de l'authentification, un terminal NFC permet d'authentifier un appareil en se connectant à un serveur pour vérifier la validité de l'appareil. De plus, l'échange d'informations entre le terminal et le mobile peuvent être entièrement chiffrés et une authentification via NFC sera plus rapide qu'une authentification via code-barres qui nous renverrait sur un site internet sur lequel entrer nos identifiants etc.

#### Grand public :

Pour une utilisation grand public comme pour les étiquettes d'un magasin ou encore la validation d'un billet pour un spectacle, les codes-barres sont à préférer. En effet, dans ces cas, il n'y a pas de communication entre 2 appareils. Seul un appareil scanner le code-barres et vérifiera sa validité. Dans le cadre d'un magasin, les codes-barres agissent comme identificateurs de produits. À leur lecteur, l'appareil doit simplement vérifier dans sa liste de produits que le code scanné existe bien. L'idée est la même pour un billet de spectacle. On va alors vérifier que le code scanné fait bien partie des codes-barres présent sur les billets vendus et on va également vérifier qu'il n'a pas déjà été scanné.

#### Lubdique :

Dans un cadre ludique, il est bien plus simple d'utiliser des codes-barres plutôt que du NFC. On pourrait par exemple mettre un QR-Code au bas d'une publicité afin de renvoyer sur un site internet. Dans un tel cas, le QR-Code a également l'avantage d'être visible. Un utilisateur voyant un QR-Code comprend directement que s'il le scanne, il sera renvoyé sur un site internet lui proposant d'avantages d'informations.

De plus, tous les téléphones ne posèdent pas une puce NFC, alors que tous ont un appareil photo. Les codes-barres peuvent donc être accessibles à plus de monde que le NFC.

#### Financier :

Les principaux avantages de la technologie des codes-barres sont sa simplicité de déployement et son prix. En effet, le prix d'un code-barre est quasiment nul. La plupart du temps, il s'agit du prix d'impression de son support, pour lequel l'ajout ou non du code-barre n'a aucune influence.

Niveau recyclage, les codes-barres sont aussi préférables puisqu'ils ne nécessitent pas de traitement particulier (ce n'est que du papier), contrairement à un tag NFC dont les composant électroniques pourraient être recyclés. Par contre, un code-barre est difficilement réutilisable, car il ne peut pas être modifié une fois imprimé. En revanche, un tag NFC peut être réécrit à volonté.

## 4.2) Question

```
Les iBeacons sont très souvent présentés comme une alternative à NFC. Pouvez-vous commenter cette affirmation en vous basant sur 2-3 exemples de cas d’utilisations (use-cases) concrets (par exemple e-paiement, second facteur d’identification, accéder aux horaires à un arrêt de bus, etc.).
```

Tout comme le NFC, les iBeacons permettent d'envoyer des informations à des utilisateurs à proximité de la balise. Cependant, même si la portée des iBeacons est plus importante que celle du NFC, les possibilités de communication sont nettement restreintes.
En effet, la mémoire contenues dans une balise iBeacon étant généralement très petite, les informations qu'elle contient sont par conséquent également limitées. On y trouve principelement un id qui, lorsqu'il est détecté, déclanche une action spécifique dans l'application programmée pour fonctionner avec cet iBeacon.

Il est vrai que les iBeacons puissent être considérés comme une alternative à NFC, mais nous pensons plus approprié de dire qu'il s'agit d'une technologie complémentaire. En effet, bien que semblables, ces deux technologies ont des utilités légèrement différentes et souvent complémentaires.

Les différences principales entre NFC et iBeacon sont:

- le coût de production: une balise iBeacon est nettement plus onéreuse qu'un tag NFC qui ne coûte que quelques dizaines de centimes!
- l'utilisation: une balise iBeacon nécessite une application propriétaire dédiée alors qu'un tag NFC peut être lu par n'importe quelle application prenant en charge la technologie NFC.

#### Exemple de use-case où les deux technologies pourraient faire l'affaire

Un iBeacon placé à l'entrée d'un hôpital peut indiquer à son application cliente d'afficher les informations médicales d'urgence du propriétaire du téléphone sur lequel elle s'exécute. Un tag NFC pourrait également offrir cette fonctionnalité mais il faudrait que le téléphone passe à proximité directe (quelques centimètres) du tag.

#### Exemple de use-case où seul NFC fonctionne

Un iBeacon possède une mémoire figée. Cela ne permet pas un échange de données comme on aurait avec un terminal NFC. On ne peut que lire les informations de la balise. Par conséquent, il n'est pas possible d'effectuer une authentification, d'établir une connexion et d'effectuer un payement via un iBeacon alors que tout cela est possible à l'aide d'un terminal NFC.
La solution serait que le iBeacon envoie un lien vers l'application associée qui elle permettrait un payement (via NFC par exemple....).

#### Exemple de use-case où seul iBeacon fonctionne

La portée plus importante d'un iBeacon comparée à celle du NFC permet de géolocaliser un appareil à l'intérieur d'un bâtiment. En effet, en utilisant plusieurs balises placées dans un bâtiment, on peut déterminer avec une précision acceptable la position de l'appareil. Cela n'est pas possible avec la portée trps faible offerte par le NFC.



## 5.2) Question

```
Une fois la manipulation effectuée, vous constaterez que les animations de la flèche ne sont pas fluides, il va y avoir un tremblement plus ou moins important même si le téléphone ne bouge pas. Veuillez expliquer quelle est la cause la plus probable de ce tremblement et donner une manière (sans forcément l’implémenter) d’y remédier. 
```

Ces tremblements peuvent être dus à plusieurs facteurs.

Tout d'abord, les capteurs peuvent être biaisés, comme on a pu le constater en utilisant un aimant pour changer la direction de la flèche.

De plus, des imprécision peuvent survenir tant à cause de la précision des données reçues des capteurs que de la précision des calculs de la matrice de rotation. 

Ces soucis de petites fluctuations des valeurs reçues des capteurs peuvent être atténuées de différentes façons. L'une des plus courante est d'appliquer un filtre passe-bas sur les données des capteurs afin d'atténuer les petites variations de valeurs sans trop altérer les variations importantes.

Ces tremblements pourraient aussi être dûs à un problème d'affichage avec OpenGL. En effet, si le taux de rafraichissement de l'image n'est pas le même que le temps de calcul d'une nouvelle matrice, cela peut engendrer des lags dans l'image qui peuvent provoquer ce genre de tremblements. Une solution pour cela serait de configurer précisément l'affichage de la boussole et sa vitesse de rafraichissement.

  