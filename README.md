# SYM - Labo 3

Auteurs : Benoît Schopfer, Antoine Rochat et Jérémie Châtillon

## 2.4) Question

```
Dans la manipulation ci-dessus, les tags NFC utilisés contiennent 4 valeurs textuelles codées en UTF-8 dans un format de message NDEF. Une personne malveillante ayant accès au porte-clés peut aisément copier les valeurs stockées dans celui-ci et les répliquer sur une autre puce NFC. 

A partir de l’API Android concernant les tags NFC4, pouvez-vous imaginer une autre approche pour rendre plus compliqué le clonage des tags NFC ? Est-ce possible sur toutes les plateformes (Android et iOS), existe-il des limitations ? Voyez-vous d’autres possibilités ? 
```



## 3.2) Question

```
Comparer la technologie à codes-barres et la technologie NFC, du point de vue d'une utilisation dans des applications pour smartphones, dans une optique : 
• Professionnelle (Authentification, droits d’accès, stockage d’une clé) 
• Grand public (Billetterie, contrôle d’accès, e-paiement) 
• Ludique (Preuves d'achat, publicité, etc.) 
• Financier (Coûts pour le déploiement de la technologie, possibilités de recyclage, etc.) 
```



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

Un iBeacon possède une mémoire figée. Cela ne permet pas un réel échange de données comme on aurait avec un terminal NFC. On ne peut que lire les informations de la balise. Par conséquent, il n'est pas possible d'effectuer un payement via un iBeacon alors que cela est possible à l'aide d'un terminal NFC.

#### Exemple de use-case où seul iBeacon fonctionne

La portée plus importante d'un iBeacon comparée à celle du NFC permet de géolocaliser un appareil à l'intérieur d'un bâtiment. En effet, en utilisant plusieurs balises placées dans un bâtiment, on peut déterminer avec une précision acceptable la position de l'appareil. Cela n'est pas possible avec la portée trps faible offerte par le NFC.



## 5.2) Question

```
Une fois la manipulation effectuée, vous constaterez que les animations de la flèche ne sont pas fluides, il va y avoir un tremblement plus ou moins important même si le téléphone ne bouge pas. Veuillez expliquer quelle est la cause la plus probable de ce tremblement et donner une manière (sans forcément l’implémenter) d’y remédier. 
```

