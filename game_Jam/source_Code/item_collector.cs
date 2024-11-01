using System.Collections;
using System.Collections.Generic;
using Unity.VisualScripting;
using UnityEngine;
using UnityEngine.UI;

public class item_collector : MonoBehaviour
{
    private int cherries = 0;
    [SerializeField] private Text cherriesText;

    private void OnTriggerEnter2D(Collider2D collision)
    {
        if (collision.gameObject.CompareTag("Cherry"))
        {
 
            Destroy(collision.gameObject);
            cherries++;
            cherriesText.text = "Cherries:" + cherries;
        }
    }
    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        
    }
}
