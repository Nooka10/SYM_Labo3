package com.labo3.sym.sym_labo3.iBeacon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.labo3.sym.sym_labo3.R;
import org.altbeacon.beacon.Beacon;

/**
 * IBeaconViewHolder utilisé pour afficher cahque iBeacons dans le RecyclerView de l'activité iBeacons affichée lorsque l'utilisateur appuie sur le bouton "IBEACON" dans
 * l'activité principale.
 */
class IBeaconViewHolder extends RecyclerView.ViewHolder {
	
	private final TextView uuidTextView;
	private final TextView rssiTextView;
	private final TextView nbMajorTextView;
	private final TextView nbMinorTextView;
	
	/**
	 * Constructeur
	 *
	 * @param itemView, la vue à utiliser pour afficher une cellule du RecyclerView.
	 */
	IBeaconViewHolder(View itemView) {
		super(itemView);
		
		// on récupère les éléments de la view
		uuidTextView = itemView.findViewById(R.id.activity_ibeacons_recyclerview_card_title);
		rssiTextView = itemView.findViewById(R.id.activity_ibeacons_recyclerview_card_rssi);
		nbMajorTextView = itemView.findViewById(R.id.activity_ibeacons_recyclerview_card_nbMajor);
		nbMinorTextView = itemView.findViewById(R.id.activity_ibeacons_recyclerview_card_nbMinor);
	}
	
	/**
	 * Bind les données de l'iBeacon reçu en paramètre afin de les afficher au bon endroit dans la cellule du RecyclerView.
	 *
	 * @param iBeacon, le iBeacon à afficher.
	 */
	void bind(Beacon iBeacon) {
		Context context = uuidTextView.getContext();
		uuidTextView.setText(context.getString(R.string.activity_ibeacons_viewholder_uuid, iBeacon.getId1().toString()));
		rssiTextView.setText(context.getString(R.string.activity_ibeacons_viewholder_rssi, String.valueOf(iBeacon.getRssi())));
		nbMajorTextView.setText(context.getString(R.string.activity_ibeacons_viewholder_nbMajor, iBeacon.getId2().toString()));
		nbMinorTextView.setText(context.getString(R.string.activity_ibeacons_viewholder_nbMinor, iBeacon.getId3().toString()));
	}
}
